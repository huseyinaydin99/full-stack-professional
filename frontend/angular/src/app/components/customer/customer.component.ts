import { Component, OnInit } from '@angular/core';
import { CustomerDTO } from '../../models/customer-dto';
import { CustomerService } from '../../services/customer/customer.service';
import { CustomerRegistrationRequest } from '../../models/customer-registration-request';
import { ConfirmationService, MessageService } from 'primeng/api';

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss']
})
export class CustomerComponent implements OnInit {

  display = false;
  operation: 'create' | 'update' = 'create';
  customers: Array<CustomerDTO> = [];
  customer: CustomerRegistrationRequest = {};

  constructor(
    private customerService: CustomerService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.findAllCustomers();
  }


  private findAllCustomers() {
    this.customerService.findAll()
    .subscribe({
      next: (data) => {
        this.customers = data;
        console.log(data);
      }
    })
  }

  save(customer: CustomerRegistrationRequest) {
    if (customer) {
      if (this.operation === 'create') {
        this.customerService.registerCustomer(customer)
        .subscribe({
          next: () => {
            this.findAllCustomers();
            this.display = false;
            this.customer = {};
            this.messageService.add(
              {severity:'success',
                summary: 'Müşteri kaydı yapıldı.',
                detail: `${customer.name} isimli müşteri kaydedildi.`
              }
            );
          }
        });
      } else if (this.operation === 'update') {
        this.customerService.updateCustomer(customer.id, customer)
        .subscribe({
          next: () => {
            this.findAllCustomers();
            this.display = false;
            this.customer = {};
            this.messageService.add(
              {
                severity:'success',
                summary: 'Müşteri kaydı güncellendi.',
                detail: `${customer.name} isimli müşteri güncellendi.`
              }
            );
          }
        });
      }
    }
  }

  deleteCustomer(customer: CustomerDTO) {
    this.confirmationService.confirm({
      header: 'Müşteri silme işlemi.',
      message: `${customer.name} isimli müşteri silinsin mi? Bu işlem geri alınamaz!`,
      accept: () => {
        this.customerService.deleteCustomer(customer.id)
        .subscribe({
          next: () => {
            this.findAllCustomers();
            this.messageService.add(
              {
                severity:'success',
                summary: 'Müşteri silme işlemi başarılı',
                detail: `${customer.name} isimli müşteri silindi.`
              }
            );
          }
        });
      }
    });
  }

  updateCustomer(customerDTO: CustomerDTO) {
    this.display = true;
    this.customer = customerDTO;
    this.operation = 'update';
  }

  createCustomer() {
    this.display = true;
    this.customer = {};
    this.operation = 'create';
  }

  cancel() {
    this.display = false;
    this.customer = {};
    this.operation = 'create';
  }
}