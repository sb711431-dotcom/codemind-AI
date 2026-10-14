package com.example.codemind.data.samples

import com.example.codemind.data.model.MlColumn
import com.example.codemind.data.model.MlDataset
import com.example.codemind.data.model.MlTaskType

object BundledDatasets {
    val irisDataset = MlDataset(
        id = "iris_flowers",
        name = "Iris Flower Classification",
        description = "Classic benchmark dataset for multiclass classification of Iris flower species.",
        filename = "iris.csv",
        taskType = MlTaskType.CLASSIFICATION,
        headers = listOf("sepal_length", "sepal_width", "petal_length", "petal_width", "species"),
        targetColumn = "species",
        columns = listOf(
            MlColumn("sepal_length", "numeric", 0, 5.84, 4.3, 7.9),
            MlColumn("sepal_width", "numeric", 0, 3.05, 2.0, 4.4),
            MlColumn("petal_length", "numeric", 0, 3.75, 1.0, 6.9),
            MlColumn("petal_width", "numeric", 0, 1.19, 0.1, 2.5),
            MlColumn("species", "categorical", 0, uniqueValues = listOf("setosa", "versicolor", "virginica"))
        ),
        rows = listOf(
            listOf("5.1", "3.5", "1.4", "0.2", "setosa"),
            listOf("4.9", "3.0", "1.4", "0.2", "setosa"),
            listOf("4.7", "3.2", "1.3", "0.2", "setosa"),
            listOf("4.6", "3.1", "1.5", "0.2", "setosa"),
            listOf("5.0", "3.6", "1.4", "0.2", "setosa"),
            listOf("5.4", "3.9", "1.7", "0.4", "setosa"),
            listOf("4.6", "3.4", "1.4", "0.3", "setosa"),
            listOf("5.0", "3.4", "1.5", "0.2", "setosa"),
            listOf("4.4", "2.9", "1.4", "0.2", "setosa"),
            listOf("4.9", "3.1", "1.5", "0.1", "setosa"),
            listOf("5.4", "3.7", "1.5", "0.2", "setosa"),
            listOf("4.8", "3.4", "1.6", "0.2", "setosa"),
            listOf("4.8", "3.0", "1.4", "0.1", "setosa"),
            listOf("4.3", "3.0", "1.1", "0.1", "setosa"),
            listOf("5.8", "4.0", "1.2", "0.2", "setosa"),
            // Versicolor
            listOf("7.0", "3.2", "4.7", "1.4", "versicolor"),
            listOf("6.4", "3.2", "4.5", "1.5", "versicolor"),
            listOf("6.9", "3.1", "4.9", "1.5", "versicolor"),
            listOf("5.5", "2.3", "4.0", "1.3", "versicolor"),
            listOf("6.5", "2.8", "4.6", "1.5", "versicolor"),
            listOf("5.7", "2.8", "4.5", "1.3", "versicolor"),
            listOf("6.3", "3.3", "4.7", "1.6", "versicolor"),
            listOf("4.9", "2.4", "3.3", "1.0", "versicolor"),
            listOf("6.6", "2.9", "4.6", "1.3", "versicolor"),
            listOf("5.2", "2.7", "3.9", "1.4", "versicolor"),
            listOf("5.0", "2.0", "3.5", "1.0", "versicolor"),
            listOf("5.9", "3.0", "4.2", "1.5", "versicolor"),
            listOf("6.0", "2.2", "4.0", "1.0", "versicolor"),
            listOf("6.1", "2.9", "4.7", "1.4", "versicolor"),
            listOf("5.6", "2.9", "3.6", "1.3", "versicolor"),
            // Virginica
            listOf("6.3", "3.3", "6.0", "2.5", "virginica"),
            listOf("5.8", "2.7", "5.1", "1.9", "virginica"),
            listOf("7.1", "3.0", "5.9", "2.1", "virginica"),
            listOf("6.3", "2.9", "5.6", "1.8", "virginica"),
            listOf("6.5", "3.0", "5.8", "2.2", "virginica"),
            listOf("7.6", "3.0", "6.6", "2.1", "virginica"),
            listOf("4.9", "2.5", "4.5", "1.7", "virginica"),
            listOf("7.3", "2.9", "6.3", "1.8", "virginica"),
            listOf("6.7", "2.5", "5.8", "1.8", "virginica"),
            listOf("7.2", "3.6", "6.1", "2.5", "virginica"),
            listOf("6.5", "3.2", "5.1", "2.0", "virginica"),
            listOf("6.4", "2.7", "5.3", "1.9", "virginica"),
            listOf("6.8", "3.0", "5.5", "2.1", "virginica"),
            listOf("5.7", "2.5", "5.0", "2.0", "virginica"),
            listOf("5.8", "2.8", "5.1", "2.4", "virginica")
        )
    )

    val housingDataset = MlDataset(
        id = "california_housing",
        name = "California Housing Prices",
        description = "Predict median home value based on income, rooms, and population metrics.",
        filename = "housing.csv",
        taskType = MlTaskType.REGRESSION,
        headers = listOf("median_income", "house_age", "ave_rooms", "population", "median_house_val"),
        targetColumn = "median_house_val",
        columns = listOf(
            MlColumn("median_income", "numeric", 0, 3.87, 0.49, 15.0),
            MlColumn("house_age", "numeric", 0, 28.6, 1.0, 52.0),
            MlColumn("ave_rooms", "numeric", 0, 5.42, 1.0, 14.2),
            MlColumn("population", "numeric", 0, 1425.0, 100.0, 5000.0),
            MlColumn("median_house_val", "numeric", 0, 206.8, 45.0, 500.0)
        ),
        rows = listOf(
            listOf("8.32", "41.0", "6.98", "322.0", "452.6"),
            listOf("8.30", "21.0", "6.23", "2401.0", "358.5"),
            listOf("7.25", "52.0", "8.28", "496.0", "352.1"),
            listOf("5.64", "52.0", "5.81", "558.0", "341.3"),
            listOf("3.84", "52.0", "6.28", "565.0", "269.7"),
            listOf("4.03", "52.0", "4.76", "413.0", "299.2"),
            listOf("3.65", "52.0", "4.93", "1094.0", "299.2"),
            listOf("3.12", "52.0", "4.79", "1157.0", "241.4"),
            listOf("2.08", "42.0", "4.29", "1206.0", "226.7"),
            listOf("3.69", "52.0", "4.97", "1551.0", "261.1"),
            listOf("3.20", "52.0", "5.47", "910.0", "281.5"),
            listOf("3.27", "52.0", "4.77", "1504.0", "241.8"),
            listOf("3.07", "52.0", "5.32", "1098.0", "213.5"),
            listOf("2.67", "52.0", "4.00", "1212.0", "191.3"),
            listOf("1.91", "52.0", "4.26", "1212.0", "159.2"),
            listOf("2.12", "50.0", "4.24", "697.0", "140.0"),
            listOf("2.77", "52.0", "5.93", "813.0", "152.5"),
            listOf("2.12", "52.0", "4.05", "648.0", "155.5"),
            listOf("1.99", "50.0", "5.34", "990.0", "158.7"),
            listOf("2.60", "52.0", "5.46", "690.0", "162.9")
        )
    )

    val customerSegmentationDataset = MlDataset(
        id = "customer_segmentation",
        name = "Customer Mall Clusters",
        description = "Discover customer behavioural groupings using unsupervised K-Means clustering.",
        filename = "customers.csv",
        taskType = MlTaskType.CLUSTERING,
        headers = listOf("age", "annual_income_k", "spending_score_1_100", "cluster"),
        targetColumn = "cluster",
        columns = listOf(
            MlColumn("age", "numeric", 0, 38.8, 18.0, 70.0),
            MlColumn("annual_income_k", "numeric", 0, 60.5, 15.0, 137.0),
            MlColumn("spending_score_1_100", "numeric", 0, 50.2, 1.0, 99.0),
            MlColumn("cluster", "categorical", 0, uniqueValues = listOf("Cluster 1", "Cluster 2", "Cluster 3"))
        ),
        rows = listOf(
            listOf("19", "15", "39", "Cluster 1"),
            listOf("21", "15", "81", "Cluster 2"),
            listOf("20", "16", "6", "Cluster 1"),
            listOf("23", "16", "77", "Cluster 2"),
            listOf("31", "17", "40", "Cluster 1"),
            listOf("22", "17", "76", "Cluster 2"),
            listOf("35", "18", "6", "Cluster 1"),
            listOf("23", "18", "94", "Cluster 2"),
            listOf("64", "19", "3", "Cluster 1"),
            listOf("30", "19", "72", "Cluster 2"),
            listOf("67", "19", "14", "Cluster 1"),
            listOf("35", "19", "99", "Cluster 2"),
            listOf("58", "20", "15", "Cluster 1"),
            listOf("24", "20", "77", "Cluster 2"),
            listOf("37", "20", "13", "Cluster 1"),
            listOf("22", "20", "79", "Cluster 2"),
            listOf("48", "60", "42", "Cluster 3"),
            listOf("42", "60", "49", "Cluster 3"),
            listOf("36", "61", "42", "Cluster 3"),
            listOf("49", "62", "42", "Cluster 3")
        )
    )

    val allDatasets = listOf(irisDataset, housingDataset, customerSegmentationDataset)
}
