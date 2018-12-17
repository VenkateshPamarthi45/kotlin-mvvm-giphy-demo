# kotlin-mvvm-giphy-demo
This android app is demo of using Kotlin with MVVM Architecture components (LiveData, ViewModel, Room).

<b>Key Features:</b>
<ol>
  <li>Showing list of trending giphys</li>
  <li>Favourite Items on other tab</li>
    <li>Search functionality added for giphys</li>
</ol>

In this project I have followed MVVM architecture used RxJava, Retrofit, Glide to display giphys images from api.
We are displaying images on pagination on scroll listener of Recyclerview.


<b>Points to consider</b>
<ol>
  <li>Handled recyclerview current position on orientation changes</li>
    <li>Handled error cases like No internet connection, no results found</li>
  <li>Espresso test cases were written to handle wait for api response and test favourite icon click</li>
</ol>
