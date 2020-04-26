package com.example.mtaa;

public class Constats {

    //MISO H - IP 192.168.0.116
    //MISO J - IP 192.168.0.129

    private static final String ROOT_URL = "http://192.168.0.116/MTAA/";
//    private static final String ROOT_URL = "http://192.168.0.129/MTAA/"; //JOZO ip
    public static final String REGISTER_URL = ROOT_URL+"createUser.php";
    public static final String LOGIN_URL = ROOT_URL+"login.php";
    public static final String ADD_TO_CART_URL = ROOT_URL+"addToCart.php";
    public static final String GET_CART_URL = ROOT_URL+"getCart.php";
    public static final String ORDER_URL = ROOT_URL+"order.php";
    public static final String GET_FOOD_HISTORY_URL = ROOT_URL+"getFoodHistory.php";
    public static final String DELETE_FROM_CART_URL = ROOT_URL+"deleteFromCart.php";
    public static final String GET_ALL_RESTAURANTS_URL = ROOT_URL+"GetAllRestaurants.php";
    public static final String GET_FOOD_FOR_RESTAURANT = ROOT_URL+"getFoodListByRestaurantID.php";
    public static final String GET_CITIES_URL = ROOT_URL+"getCities.php";
    public static final String RESSET_PASSWORD_URL = ROOT_URL+"passwordReset.php";
    public static final String RESSET_EMAIL_URL = ROOT_URL+"emailReset.php";

}
