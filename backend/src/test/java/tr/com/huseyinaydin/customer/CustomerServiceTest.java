package tr.com.huseyinaydin.customer;

import tr.com.huseyinaydin.exception.DuplicateResourceException;
import tr.com.huseyinaydin.exception.RequestValidationException;
import tr.com.huseyinaydin.exception.ResourceNotFoundException;
import tr.com.huseyinaydin.s3.S3Buckets;
import tr.com.huseyinaydin.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

//بسم الله الرحمن الرحيم
/**
 *
 * @author Huseyin_Aydin
 * @since 1994
 * @category Spring Boot, Java, Backend, Frontend, FullStack...
 *
 */

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets s3Buckets;
    private CustomerService underTest;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao, customerDTOMapper, passwordEncoder, s3Service, s3Buckets);
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Hüseyin", "huseyinaydin99@gmail.com", "password", 29, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // Given
        int id = 10;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id)).isInstanceOf(ResourceNotFoundException.class).hasMessage("[%s] Id'li müşteri bulunamadı.".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "huseyinaydin99@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Hüseyin", email, "password", 29, Gender.MALE);

        String passwordHash = "¢5054mn;t;lsx";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        // Given
        String email = "huseyinaydin99@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", email, "password", 19, Gender.MALE);

        // When
        assertThatThrownBy(() -> underTest.addCustomer(request)).isInstanceOf(DuplicateResourceException.class).hasMessage("E-posta zaten var dostum.");

        // Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 10;

        when(customerDao.existsCustomerById(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);
        // Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowDeleteCustomerByIdNotExists() {
        // Given
        int id = 10;

        when(customerDao.existsCustomerById(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id)).isInstanceOf(ResourceNotFoundException.class).hasMessage("[%s] Id'li müşteri bulunamadı.".formatted(id));

        // Then
        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomersProperties() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Hüseyin", "huseyinaydin99@gmail.com", "password", 29, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "huseyinaydin99@outlook.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("M.Elbistan", newEmail, 70);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Hüseyin", "huseyinaydin99@gmail.com", "password", 29, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Cumali", null, null);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Hüseyin", "huseyinaydin99@gmail.com", "password", 29, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "huseyinaydin99@outlook.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Hüseyin", "huseyinaydin99@gmail.com", "password", 29, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, 22);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Hüseyin", "huseyinaydin99@gmail.com", "password", 29, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "huseyinaydin99@gmail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest)).isInstanceOf(DuplicateResourceException.class).hasMessage("E-posta zaten var.");

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Hüseyin", "huseyinaydin99@gmail.com", "password", 29, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest)).isInstanceOf(RequestValidationException.class).hasMessage("Veri değişimi bulunamadı. Aynı kaydı güncellemeye çalışıyorsun.");

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void canUploadProfileImage() {
        // Given
        int customerId = 10;

        when(customerDao.existsCustomerById(customerId)).thenReturn(true);

        byte[] bytes = "Selamun Aleyküm hacı abi.".getBytes();

        MultipartFile multipartFile = new MockMultipartFile("file", bytes);

        String bucket = "musteri-kovasi";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        underTest.uploadCustomerProfileImage(customerId, multipartFile);

        // Then
        ArgumentCaptor<String> profileImageIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(customerDao).updateCustomerProfileImageId(profileImageIdArgumentCaptor.capture(), eq(customerId));

        verify(s3Service).putObject(bucket, "profile-images/%s/%s".formatted(customerId, profileImageIdArgumentCaptor.getValue()), bytes);
    }

    @Test
    void cannotUploadProfileImageWhenCustomerDoesNotExists() {
        // Given
        int customerId = 10;

        when(customerDao.existsCustomerById(customerId)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(
                customerId, mock(MultipartFile.class))
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("[" + customerId + "] Id'li müşteri bulunamadı dostum.");

        // Then
        verify(customerDao).existsCustomerById(customerId);
        verifyNoMoreInteractions(customerDao);
        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }

    @Test
    void cannotUploadProfileImageWhenExceptionIsThrown() throws IOException {
        // Given
        int customerId = 10;

        when(customerDao.existsCustomerById(customerId)).thenReturn(true);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        String bucket = "musteri-kovasi";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        assertThatThrownBy(() -> {
            underTest.uploadCustomerProfileImage(customerId, multipartFile);
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("Profil resmi yüklenirken hata oluştu.")
                .hasRootCauseInstanceOf(IOException.class);

        // Then
        verify(customerDao, never()).updateCustomerProfileImageId(any(), any());
    }

    @Test
    void canDownloadProfileImage() {
        // Given
        int customerId = 10;
        String profileImageId = "2222";
        Customer customer = new Customer(
                customerId,
                "Hüseyin",
                "huseyinaydin99@gmail.com",
                "password",
                29,
                Gender.MALE,
                profileImageId
        );
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String bucket = "musteri-kovasi";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        byte[] expectedImage = "image".getBytes();

        when(s3Service.getObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageId))
        ).thenReturn(expectedImage);

        // When
        byte[] actualImage = underTest.getCustomerProfileImage(customerId);

        // Then
        assertThat(actualImage).isEqualTo(expectedImage);
    }

    @Test
    void cannotDownloadWhenNoProfileImageId() {
        // Given
        int customerId = 10;
        Customer customer = new Customer(
                customerId,
                "Hüseyin",
                "huseyinaydin99@gmail.com",
                "password",
                19,
                Gender.MALE
        );

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("[%s] Id'li müşterinin profil resmi bulunamadı dostum.".formatted(customerId));

        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }

    @Test
    void cannotDownloadProfileImageWhenCustomerDoesNotExists() {
        // Given
        int customerId = 10;

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("[%s] Id'li müşteri bulunamadı dostum.".formatted(customerId));

        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }
}