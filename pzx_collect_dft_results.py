#!/usr/bin/env python3
import os
import warnings
from pathlib import Path
from pymatgen.io.vasp.outputs import Oszicar, Vasprun
from monty.serialization import dumpfn


def parse_vasp_dir(base_dir: str, idx: int, *, check_electronic_convergence: bool = True):
    """
    Parse VASP results from a single directory (no magnetization parsing).

    Returns dict with:
      - structure
      - total_energy
      - energy_per_atom
      - forces
      - stress
    """
    oszicar_path = os.path.join(base_dir, "OSZICAR")
    vasprun_path = os.path.join(base_dir, "vasprun.xml")

    if not os.path.exists(oszicar_path) or not os.path.exists(vasprun_path):
        print(f"⚠️  Missing OSZICAR or vasprun.xml in {base_dir}")
        return None

    try:
        oszicar = Oszicar(oszicar_path)
        vasprun = Vasprun(
            vasprun_path,
            parse_dos=False,
            parse_eigen=False,
            parse_projected_eigen=False,
            parse_potcar_file=False,
            exception_on_bad_xml=False,
        )
    except Exception as e:
        warnings.warn(f"❌ Failed to parse {base_dir}: {e}")
        return None

    n_atoms = len(vasprun.ionic_steps[0]["structure"])

    dataset = {
        "struc_id": idx,
        "structure": [],
        "total_energy": [],
        "energy_per_atom": [],
        "forces": [],
        "stress": [],
    }

    for ionic_step in vasprun.ionic_steps:
        # 跳过未收敛电子步
        if check_electronic_convergence and len(ionic_step["electronic_steps"]) >= vasprun.parameters.get("NELM", 0):
            continue
        dataset["structure"].append(ionic_step["structure"])
        dataset["total_energy"].append(ionic_step["e_0_energy"])
        dataset["energy_per_atom"].append(ionic_step["e_0_energy"] / n_atoms)
        dataset["forces"].append(ionic_step["forces"])
        if "stress" in ionic_step:
            dataset["stress"].append(ionic_step["stress"])

    if not dataset["total_energy"]:
        print(f"⚠️  No valid ionic steps parsed in {base_dir}")
        return None

    return dataset


def main():
    # 查找所有 OUTCAR 所在目录（当前目录起）
    paths = [p.parent for p in Path.cwd().rglob("OUTCAR")]
    if not paths:
        print("❌ No OUTCAR found. Please run this script in a directory containing VASP results.")
        return

    all_data = []
    print(f"🧭 Found {len(paths)} OUTCAR directories. Starting collection...")

    for i, folder in enumerate(paths):
        print(f"[{i+1}/{len(paths)}] Parsing: {folder}")
        res = parse_vasp_dir(str(folder), idx=i)
        if res:
            all_data.append(res)

    dumpfn(all_data, "safe_results.json")
    print(f"\n✅ Finished! Parsed {len(all_data)} directories. Results saved to safe_results.json")


if __name__ == "__main__":
    main()
