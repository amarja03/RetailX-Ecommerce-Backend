package com.example.eshopee.config;

public class AppConstants {
	
	public static final String PAGE_NUMBER = "0";
	public static final String PAGE_SIZE = "5";
	public static final String SORT_CATEGORIES_BY = "categoryId";
	public static final String SORT_PRODUCTS_BY = "productId";
	public static final String SORT_USERS_BY = "userId";
	public static final String SORT_ORDERS_BY = "totalAmount";
	public static final String SORT_DIR = "asc";
	public static final Long ADMIN_ID = 101L;
	public static final Long USER_ID = 102L;
	public static final long JWT_TOKEN_VALIDITY = 86400000;
	public static final String[] PUBLIC_URLS = { "/swagger-ui.html","/v3/api-docs/**", "/swagger-ui/**", "/api/register/**", "/api/login", "/api/health" };
	public static final String[] USER_URLS = { "/api/public/**" };
	public static final String[] ADMIN_URLS = { "/api/admin/**" };

	public static final String JWT_SECRET_KEY = "JWT_SECRET";
	public static final String JWT_SECRET_DEFAULT = "cl$ksc^nas%lkdm!cz@xclk#as&dmka;s28*392da%jskd#n9827@3498%";
	public static final String JWT_HEADER = "Authorization";

//	public static
	
}
