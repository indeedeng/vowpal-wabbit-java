---
title: "Prepare Movie Lens Dataset"
output: html_document
---

Data set source
===============

Dataset for this integration test is "MovieLens 1M Dataset"" famous in recomendation systems community.

I use 1M dataset; you can download it at [http://grouplens.org/datasets/movielens/1m/].

In this script we do some preparation:

- Join movies.dat, ratings.dat  and users.dat into single dataframe
- Split this dataset by time: that's it; task will be to predict ratings in last few days
- Calculate baseline score by using avarage rating calculated on train sets



Adding libraries
----------------
```{r}
library(dplyr)
```

Reading data
------------
Before I converted ".dat" format to ".csv" format using sed:
```
sed 's/::/,/g' ratings.dat > ratings.csv
```

```{r}
ratings = read.csv("ratings.csv", header=FALSE)
names(ratings) = c('user_id', 'movie_id', 'rating', 'timestamp')
movies = read.csv("movies.csv", header=FALSE)
names(movies) = c('movie_id', 'm_title', 'm_genres')
users = read.csv("users.csv", header=FALSE)
names(users) = c('user_id', 'u_gender', 'u_age', 'u_ocupation', 'u_zip_code')

```

Joining data
------------

```{r}
dataset = ratings %>%
  inner_join(users, by = "user_id") %>%
  inner_join(movies, by = "movie_id") %>%
  transmute(rating, timestamp, user_id, movie_id, 
            u_gender, u_age, u_ocupation, u_zip_code, 
            m_title, m_genres) %>%
  arrange(timestamp)
```

Splitting into train and test
-----------------------------

Let's predict last 10% of dataset sortered by time

```{r}
train_max_row_number = round(count(dataset) * 0.9) %>% .$n

train = dataset %>% filter(row_number() <= train_max_row_number)
test = dataset %>% filter(row_number() > train_max_row_number)
```

Let's also check how overlap users and movies sets between train and test
```{r}
dataset %>% summarise(
  n_distinct(user_id), 
  n_distinct(movie_id), 
  n_distinct(paste(user_id, movie_id)))
train %>% summarise(
  n_distinct(user_id), 
  n_distinct(movie_id), 
  n_distinct(paste(user_id, movie_id)))
test %>% summarise(
  n_distinct(user_id), 
  n_distinct(movie_id), 
  n_distinct(paste(user_id, movie_id)))
```

Users overlap = train users distinct count + test users distinct count - full dataset users distinct count
= 6011 + 1209 - 6040 = 1180

Movies overlap = train movies distinct count + test movies distinct count - full dataset movies distinct count
= 3678 + 3407 - 3706 = 3379

Interaction overlap is zero

Calculate baseline
------------------

```{r}
rating = train %>% summarise(val = mean(rating)) %>% .$val

rmse = sqrt(mean((test$rating - rating)^2))
```

rmse = 1.08929

Storing data
------------
```{r}
write.csv(train, file = "train.csv", row.names = FALSE, quote = FALSE)
write.csv(test, file = "test.csv", row.names = FALSE, quote = FALSE)
```

After I wrote these files I zipped them with gzip command

Output format
=============

10-columns csv file.

Columns description:

1. rating - are made on a 5-star scale (whole-star ratings only) - this is target variable
2. timestamp - is represented in seconds since the epoch as returned by time(2)
3. user_id - range between 1 and 6040
4. movie_id - range between 1 and 3952
5. u_gender - Gender is denoted by a "M" for male and "F" for female
6. u_age - Age is chosen from the following ranges: [1]
7. u_ocupation - Occupation is chosen from the following choices: [2]
8. u_zip_code - Zip-code
9. m_title - Titles are identical to titles provided by the IMDB (including year of release)
10 m_genres - Genres are pipe-separated and are selected from the following genres: [3]


[1] - _Ages ranges:_

*  1:  "Under 18"
* 18:  "18-24"
* 25:  "25-34"
* 35:  "35-44"
* 45:  "45-49"
* 50:  "50-55"
* 56:  "56+"

[2] - _Ocupation choices:_

*  0:  "other" or not specified
*  1:  "academic/educator"
*  2:  "artist"
*  3:  "clerical/admin"
*  4:  "college/grad student"
*  5:  "customer service"
*  6:  "doctor/health care"
*  7:  "executive/managerial"
*  8:  "farmer"
*  9:  "homemaker"
* 10:  "K-12 student"
* 11:  "lawyer"
* 12:  "programmer"
* 13:  "retired"
* 14:  "sales/marketing"
* 15:  "scientist"
* 16:  "self-employed"
* 17:  "technician/engineer"
* 18:  "tradesman/craftsman"
* 19:  "unemployed"
* 20:  "writer"

[3] - _Genres:_

* Action
* Adventure
* Animation
* Children's
* Comedy
* Crime
* Documentary
* Drama
* Fantasy
* Film-Noir
* Horror
* Musical
* Mystery
* Romance
* Sci-Fi
* Thriller
* War
* Western