import os


def try_read(abs_file: str) -> list:
    try:
        with open(abs_file, "r") as rf:
            return rf.readlines()
    except UnicodeDecodeError:
        try:
            with open(abs_file, "r", encoding="utf-8") as rf:
                return rf.readlines()
        except UnicodeDecodeError:
            return []


def traverse(base: str, file: str, results: list, fmt_list: list, main_file=None):
    abs_file = os.path.join(base, file)
    if os.path.isdir(abs_file):
        lst = os.listdir(abs_file)
        for f in lst:
            traverse(abs_file, f, results, fmt_list, main_file)
    elif os.path.isfile(abs_file):
        if len(fmt_list) > 0:
            found = False
            for fmt in fmt_list:
                if file.endswith(fmt):
                    found = True
                    break
            if not found:
                return
        lines = try_read(abs_file)
        res_lines = []
        for line in lines:
            s = line.strip()
            if len(s) > 0:
                res_lines.append(line)
        text = "".join(res_lines)
        if len(text) > 0 and not text.endswith("\n"):
            text = text + "\n"
        if file == main_file:
            results.insert(0, text)
        else:
            results.append(text)


if __name__ == '__main__':
    base_dir = input("Root directory (no input if use current dir):").strip()
    entry_file = input("Main file (no input if none):").strip()
    output_name = input("Name of output file:").strip()
    formats_str = input("Formats, separated by space (no input if get all):").strip()
    formats = [part.strip() for part in formats_str.split(" ")]
    if base_dir == "":
        base_dir = os.getcwd()
    abs_base = os.path.dirname(os.path.abspath(base_dir))
    result_lst = []
    traverse(abs_base, base_dir, result_lst, formats, entry_file)
    res_str = "".join(result_lst)
    if len(output_name) == 0:
        output_name = "output.txt"
    with open(output_name, "w", encoding="utf-8") as wf:
        wf.write(res_str)
