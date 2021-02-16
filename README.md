# om-annotsv-svc: Optical Mapping and AnnotSV Structural Variant Comparator

A software tool for comparison of structural variants identified by optical mapping technology (Bionano Genomics) and long-read next-generation sequencing technologies i.e. 10x Genomics, Oxford Nanopore Technologies and Pacific Biosciences. Structural variants produced by long-read next-generation sequencing technologies are analyzed using AnnotSV software tool. The tool employes variance between Bionano and AnnotSV genomic coordinates for the beginning and end of individual SVs. Various types of filtering, such as overlapping gene filter, variant type filter or distance sum variance filter can be used. The tool is provided as CLI application and is platform independent.

## Requirements
Java Runtime Environment 8 or higher.

## Command line arguments
| Parameter | Long | Type | Default | Description | Required |
| --- | --- | --- | --- | --- | --- |
| -a | --annotsv_input | String || AnnotSV analysis result TSV file path. | \* | 
| -b | --bionano_input | String || Bionano Genomics analysis pipeline result SMAP file path.  | \* |
| -d | --variant_distance | Integer || Distance sum variance filter (i.e. number of bases difference between variant from OM and AnnotSV) ||
| -g | --gene_intersection | Boolean | false | Overlapping genes filter (i.e. variants with non-overlapping genes are filtered out) ||
| -o | --output | String || Output result file path. | \* |
| -t | --variant_type | String || Variant type filter. Any combination of BND,CNV,DEL,INS,DUP,INV,UNK. ||

## Example of usage
Some basic example usage of structural variant comparator follows. More detailed usage with sample data and results are presented in sample package in <b>./example</b> directory in this repository. Each directory in sample package contains README.txt file where can be found detailed description of each file. Runnable binary version of the application is presented in <b>./bin</b> directory of the repository.

### Basic usage
In basic setup application compares all SVs contained in AnnotSV and Bionano result files. No filters are applied here.

```console
java -jar om-annotsv-svc.jar -a annotsv_result.tsv -b bionano_pipeline_result.smap -o result.csv
```

### Variance distance sum filter
Following command filters out variants which have distance sum variance greater than 50000 bases.

```consolev
java -jar ovm-annotsv-svc.jar -a annotsv_result.tsv -b bionano_pipeline_result.smap -d 50000 -o result.csv 
```

### Overlapping genes filter 
Following command filters out variants which have distance sum variance greater than 50000 bases and have no genes in overlap.

```console
java -jar om-annotsv-svc.jar -a annotsv_result.tsv -b bionano_pipeline_result.smap -d 50000 -g -o result.csv 
```

### Variant type filter
Following command will analyze only translocations (BND), deletions (DEL) and insertions (INS). Other variant types are ignored.

```console
java -jar om-annotsv-svc.jar -a annotsv_result.tsv -b bionano_pipeline_result.smap -t "BND,DEL,INS" -o result.csv 
```

## Contact
If you have any problem or questions about the software tool, please contact us.

Tomáš Novosád (tomas.novosad@vsb.cz)

Jakub Savara (jakub.savara@vsb.cz)

