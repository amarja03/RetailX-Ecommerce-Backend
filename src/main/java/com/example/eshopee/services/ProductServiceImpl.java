package com.example.eshopee.services;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.eshopee.entites.Cart;
import com.example.eshopee.entites.Category;
import com.example.eshopee.entites.Product;
import com.example.eshopee.exceptions.APIException;
import com.example.eshopee.exceptions.ResourceNotFoundException;
import com.example.eshopee.payloads.CartDTO;
import com.example.eshopee.payloads.ProductDTO;
import com.example.eshopee.payloads.ProductResponse;
import com.example.eshopee.repositories.CartRepo;
import com.example.eshopee.repositories.CategoryRepo;
import com.example.eshopee.repositories.ProductRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private CartRepo cartRepo;

	@Autowired
	private CartService cartService;

	@Autowired
	private FileService fileService;

	@Autowired
	private ModelMapper modelMapper;

	@Value("${project.image}")
	private String path;

	@Override
	public ProductDTO addProduct(Long categoryId, Product product) {

		Category category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

		boolean isProductNotPresent = true;

		List<Product> products = category.getProducts();

		//TODO: Can we directly find the count of products with the given name and description rather than fetching all the products and iterating over them?
        for (Product prod : products) {
            if (prod.getProductName().equals(product.getProductName())
                    && prod.getDescription().equals(product.getDescription())) {

                isProductNotPresent = false;
                break;
            }
        }

		if (isProductNotPresent) {
			product.setImage("default.png");

			product.setCategory(category);

			double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
			product.setSpecialPrice(specialPrice);

			Product savedProduct = productRepo.save(product);

			return modelMapper.map(savedProduct, ProductDTO.class);
		} else {
			throw new APIException("Product already exists !!!");
		}
		/*
		- ProductDTO addProduct(Long categoryId, Product product)
			i. Fetch the category from the categoryRepo using the categoryId.
			ii. Check if the product already exists in the category.
			iii. If the product doesn't exist, set the image of the product to "default.png".
			iv. Set the category of the product.
			v. Calculate the special price of the product.
			vi. Save the product in the productRepo.
			vii. Return the saved product as ProductDTO.

		 */
	}

	@Override
	public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Product> pageProducts = productRepo.findAll(pageDetails);

		List<Product> products = pageProducts.getContent();

		return getProductResponse(pageProducts, products);
		/*
		- ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder)
			i. Create a Sort object using the sortBy and sortOrder.
			ii. Create a Pageable object using the pageNumber, pageSize, and the Sort object.
			iii. Fetch all the products using the productRepo.
			iv. If the products list is empty, throw an APIException.
			v. Return the ProductResponse object by passing the pageProducts and products to the getProductResponse method.
		 */
	}

	@Override
	public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
											String sortOrder) {

		Category category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Product> pageProducts = productRepo.findAll(pageDetails);

		List<Product> products = pageProducts.getContent();

		if (products.isEmpty()) {
			throw new APIException(category.getCategoryName() + " category doesn't contain any products !!!");
		}

		return getProductResponse(pageProducts, products);
	}

	private ProductResponse getProductResponse(Page<Product> pageProducts, List<Product> products) {
		List<ProductDTO> productDTOs = products.stream().map(p -> modelMapper.map(p, ProductDTO.class))
				.collect(Collectors.toList());

		ProductResponse productResponse = new ProductResponse();

		productResponse.setContent(productDTOs);
		productResponse.setPageNumber(pageProducts.getNumber());
		productResponse.setPageSize(pageProducts.getSize());
		productResponse.setTotalElements(pageProducts.getTotalElements());
		productResponse.setTotalPages(pageProducts.getTotalPages());
		productResponse.setLastPage(pageProducts.isLast());

		return productResponse;
		/*
		- private ProductResponse getProductResponse(Page<Product> pageProducts, List<Product> products)
			i. Convert the products list to a list of ProductDTO using the modelMapper.
			ii. Create a ProductResponse object.
			iii. Set the content, pageNumber, pageSize, totalElements, totalPages, and lastPage of the ProductResponse object.

		 */
	}

	@Override
	public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Product> pageProducts = productRepo.findByProductNameLike(keyword, pageDetails);

		List<Product> products = pageProducts.getContent();

		if (products.isEmpty()) {
			throw new APIException("Products not found with keyword: " + keyword);
		}

		return getProductResponse(pageProducts, products);
		/*
		- ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder)
			i. Create a Sort object using the sortBy and sortOrder.
			ii. Create a Pageable object using the pageNumber, pageSize, and the Sort object.
			iii. Fetch the products using the productRepo by the keyword.
			iv. If the products list is empty, throw an APIException.
			v. Return the ProductResponse object by passing the pageProducts and products to the getProductResponse method.
		 */
	}

	@Override
	public ProductDTO updateProduct(Long productId, Product product) {
		/*
		- ProductDTO updateProduct(Long productId, Product product)

			i. Fetch the product from the productRepo using the productId.
			ii. If the product is not found, throw an APIException.
			iii. Set the image, productId, and category of the product.
			iv. Calculate the special price of the product.
			v. Save the product in the productRepo.
			vi. Fetch the carts containing the product using the cartRepo.
			vii. Convert the carts list to a list of CartDTO.
			viii. Iterate over the cartDTOs list and update the product in the carts using the cartService.
			ix. Return the saved product as ProductDTO.

		 */
		Product productFromDB = productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		if (productFromDB == null) {
			throw new APIException("Product not found with productId: " + productId);
		}

		product.setImage(productFromDB.getImage());
		product.setProductId(productId);
		product.setCategory(productFromDB.getCategory());

		double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
		product.setSpecialPrice(specialPrice);

		Product savedProduct = productRepo.save(product);

		List<Cart> carts = cartRepo.findCartsByProductId(productId);

		List<CartDTO> cartDTOs = carts.stream().map(cart -> {
			CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

			List<ProductDTO> products = cart.getCartItems().stream()
					.map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

			cartDTO.setProducts(products);

			return cartDTO;

		}).collect(Collectors.toList());

		cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

		return modelMapper.map(savedProduct, ProductDTO.class);

	}

	@Override
	public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
		Product productFromDB = productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		if (productFromDB == null) {
			throw new APIException("Product not found with productId: " + productId);
		}

		String fileName = fileService.uploadImage(path, image);

		productFromDB.setImage(fileName);

		Product updatedProduct = productRepo.save(productFromDB);

		return modelMapper.map(updatedProduct, ProductDTO.class);
	}

	@Override
	public String deleteProduct(Long productId) {


		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		List<Cart> carts = cartRepo.findCartsByProductId(productId);

		carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

		productRepo.delete(product);

		return "Product with productId: " + productId + " deleted successfully !!!";
		/*
		- String deleteProduct(Long productId)
		 i. Fetch the product from the productRepo using the productId.
		 ii. If the product is not found, throw an APIException.
		 iii. Fetch the carts containing the product using the cartRepo.
		 iv. Iterate over the carts list and delete the product from the carts using the cartService.
		 v. Delete the product from the productRepo.
		 vi. Return a success message.

		 */
	}

}
