---
title: "Prepare Twitter Sentiment Analysis Dataset"
output: html_document
---

Data set source
===============

This dataset was downloaded from [http://thinknook.com/twitter-sentiment-analysis-training-corpus-dataset-2012-09-22/]

First I  cleaned up downloaded file to make it to fix format so we could load it in R.
```
$ cat Sentiment\ Analysis\ Dataset.csv | \
sed 's/\t/ /g' | \
awk -F',' '{
line = $2"\t"$4; 
for (c=5; c<=NF; c++) {
line = line","$c
} 
print line  
}' > sentiment-analysis.cleanedup.tsv
```

Adding libraries
----------------
```{r}
library(dplyr)
library(caret)
```

Reading data
------------

```{r}
dataset = read.csv("sentiment-analysis.cleanedup.tsv", 
                   quote = "", sep = '\t', header=TRUE, stringsAsFactors = FALSE)
```

Sample
------

```{r}
sampled_dataset = dataset %>% sample_frac(0.5)
```

Split into train and test
-------------------------
```{r}
partition = createDataPartition(sampled_dataset$Sentiment, p = .7, list = FALSE)
train_set = sampled_dataset[partition,]
test_set = sampled_dataset[-partition,]
```

Write files
-----------

```{r}
write.table(train_set, sep = "\t", file = "train.tsv", row.names = FALSE, quote = FALSE)
write.table(test_set, sep = "\t", file = "test.tsv", row.names = FALSE, quote = FALSE)
```

After I wrote these files I zipped them with gzip command.

Output format
=============

2-columns tsv file.

Columns description:

1. sentiment - 1 or 0
2. tweet text

