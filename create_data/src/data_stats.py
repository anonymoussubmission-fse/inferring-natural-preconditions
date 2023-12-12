import data_collection_utils as utils
import sys


if len(sys.argv) > 1:
    proj = sys.argv[1]
else:
    proj = None

#utils.print_stats_standalone("29_apbsmem")
#utils.print_stats_standalone("2_a4j")
utils.print_stats_standalone(proj)
#utils.print_stats_standalone("100_bigint")
print("\n".join(utils.spot_check(utils.SampleType.interesting, proj)))

