package tr.com.huseyinaydin.customer;

import java.util.List;
import java.util.Optional;

//بسم الله الرحمن الرحيم
/**
 *
 * @author Huseyin_Aydin
 * @since 1994
 * @category Spring Boot, Java, Backend, Frontend, FullStack...
 *
 */

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer customerId);
    void insertCustomer(Customer customer);
    boolean existsCustomerWithEmail(String email);
    boolean existsCustomerById(Integer customerId);
    void deleteCustomerById(Integer customerId);
    void updateCustomer(Customer update);
    Optional<Customer> selectUserByEmail(String email);
    void updateCustomerProfileImageId(String profileImageId, Integer customerId);
}