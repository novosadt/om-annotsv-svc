# om-annotsv-svc: Optical Mapping and AnnotSV Structural Variant Comparator

A software tool for comparison of structural variants identified by optical mapping technology (Bionano Genomics) and 3rd generation sequencing technologies i.e. 10xGenomics, Oxford Nanopore Technologies and Pacific Biosciences. Structural variants produced by 3rd generation sequencing technologies are analyzed using AnnotSV software tool. The tool employes the distance sum variance between the Bionano and AnnotSV genomic coordinates for the beginning and end of individual SVs. The tools is provided as CLI application, platform independent.

## Requirements
Java Runtime Environment 8 or higher.

## Command line arguments
| Parameter | Long | Type | Default | Description | Required |
| --- | --- | --- | --- | --- | --- |
| -a | --annotsv_input | String || AnnotSV analysis result TSV file path. | \* | 
| -b | --bionano_input | String || Bionano Genomics analysis pipeline result SMAP file path.  | \* |
| -d | --variant_distance | Integer || Distance sum variance filter (i.e. number of bases difference between variant from OM and AnnotSV) ||
| -g | --gene_intersection | Boolean | false | Common genes filter (i.e. variants with non-overlapping genes are filtered out) ||
| -o | --output | String || Output result file path. | \* |
| -t | --variant_type | String || Variant type filter. Any combination of BND,CNV,DEL,INS,DUP,INV,UNK. ||



