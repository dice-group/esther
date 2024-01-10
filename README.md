# ESTHER

Source code of [ESTHER - Using Compositional Embeddings for Fact Checking](https://papers.dice-research.org/2021/ISWC2021_Esther/ESTHER_public.pdf)

Esther currently supports TransE, RotatE and DensE embeddings.

## Adding new KGE models
Create a class that extends [EmbeddingModel](https://github.com/dice-group/esther/blob/master/src/main/java/org/dice_group/models/EmbeddingModel.java).

## How to run
1. Generate KGE for the knowledge graph (we're only interested in relations embeddings)
2. Upload the corresponding knowledge graph to a SPARQL endpoint
3. Run ESTHER

### Running ESTHER

ESTHER was developed using Java 17.

You can run ESTHER with maven's exec plugin :

``` 
mvn exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="org.dice_group.main.Launcher" -Dexec.args="-m S -d TransE/ -e TransE -k 200 -se http://localhost:8890/sparql/ -s experiment_result -l 4 -loops -f facts.nt
```

Or through its jar file:

```
java -jar esther.jar -m S -d TransE/ -e TransE -k 200 -se http://localhost:8890/sparql/ -s experiment_result -l 4 -loops -f facts.nt
```

## Parameters

<table>
  <tr><th align="left">Parameter</th><th>Required</th><th>Default</th><th>Description</th></tr>
  <tr><th align="left">--data, -d</th><td>True</td><td>NA</td><td>Folder path where the embedding files and the relations dictionary reside</td></tr>
  <tr><th align="left">--save, -s</th><td>True</td><td>NA</td><td>Saving file name (It will be saved under the folder path previously specified)</td></tr>
  <tr><th align="left">--facts, -f</th><td>True</td><td>NA</td><td>File path of the reified statements to be checked.</td></tr>
  <tr><th align="left">--dict</th><td>False</td><td>NA</td><td> File path of the relations dictionary. If it's not provided, it will be created from the embeddings file.</td></tr>
  <tr><th align="left">-ds</th><td>False</td><td>None</td><td>Dataset name {FB, WN} (only needed if the dictionary does not contain fully formed URIs)</td></tr>
  <tr><th align="left">--topk, -k</th><td>False</td><td>100</td><td>Maximum number of metapaths</td></tr>
  <tr><th align="left">--matrix, -m</th><td>False</td><td>I</td><td>The comparison mode {S, SU, ND, NDS, I}</td></tr>
  <tr><th align="left">--emb-model,-e</th><td>False</td><td>TransE</td><td>The embedding model used {TransE, RotatE, DensE}</td></tr>
  <tr><th align="left">--endpoint, -se</th><td>True</td><td>NA</td><td>The SPARQL endpoint where the knowledge graph is hosted</td></tr>
  <tr><th align="left">-l</th><td>False</td><td>3</td><td>Maximum path length</td></tr>
  <tr><th align="left">-loops</th><td>False</td><td>False</td><td>If loops are to be allowed in paths</td></tr>
</table>


<!-- Commented
- **--data, -d**: Folder path where the embedding files and the relations dictionary reside. (required)
- **--save, -s**: Saving file name (It will be saved under the folder path previously specified) (required)
- **--facts, -f**: File path of the facts to be checked. (required)
- **--dict**: File path of the relations dictionary. (required)
- **-ds**: Dataset name {FB, WN} (only needed if the dictionary does not contain fully formed URIs)
- **--topk, -k**: Maximum number of metapaths. (Default = 100)
- **--matrix, -m**: The comparison mode {S, SU, ND, NDS, I}
- **--emb-model,-e**: The embedding model used {TransE, RotatE, DensE}
- **--endpoint, -se**: The SPARQL endpoint where the knowledge graph is hosted. (required)
- **-l**: Maximum path length. (Default = 3)
- **-loops**: Specify if you want loops to be allowed in paths.
-->

## How to cite

``` 
@InProceedings{esther2021,
  author="da Silva, Ana Alexandra Morim
  and R{\"o}der, Michael
  and Ngomo, Axel-Cyrille Ngonga",
  title="Using Compositional Embeddings for Fact Checking",
  booktitle="The Semantic Web -- ISWC 2021",
  year="2021",
  publisher="Springer International Publishing",
  address="Cham",
  pages="270--286",
  isbn="978-3-030-88361-4"
}
```
