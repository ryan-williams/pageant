#!/usr/bin/env python

from math import ceil
from subprocess import check_call
import sys

# Usage:
#
#   $ scripts/run-on-gcloud <num cores> <dataproc spark args> -- <app args>

num_cores = int(sys.argv[1])

machine_type = "n1-standard-4"
cores_per_machine = 4
total_num_workers = ceil(num_cores / cores_per_machine)
num_workers = 2
num_preemtible_workers = max(0, total_num_workers - num_workers)

cluster_name = "pageant"
pageant_jar = "gs://hammerlab-lib/pageant-c482335.jar"
main_class = "org.hammerlab.pageant.coverage.CoverageDepth"

print("Setting up cluster with %d workers and %d pre-emptible workers" % (num_workers, num_preemtible_workers))
check_call(
    [
        "gcloud", "dataproc", "clusters", "create", cluster_name,
        "--master-machine-type", machine_type,
        "--worker-machine-type", machine_type,
        "--num-workers", str(num_workers),
        "--num-preemptible-workers", str(num_preemtible_workers)
    ]
)

try:
    print("Submit job")
    check_call(
        [
            "gcloud", "dataproc", "jobs", "submit", "spark",
            "--cluster", cluster_name,
            "--class", main_class,
            "--jars", pageant_jar
        ] +
        sys.argv[2:]
    )
finally:
    print("Tearing down cluster")
    check_call(
        [
            "gcloud", "dataproc", "clusters", "delete", cluster_name
        ]
    )


