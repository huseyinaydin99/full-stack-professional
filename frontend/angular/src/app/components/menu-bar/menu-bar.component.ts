import { Component } from '@angular/core';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-menu-bar',
  templateUrl: './menu-bar.component.html',
  styleUrls: ['./menu-bar.component.scss']
})
export class MenuBarComponent {

  menu: Array<MenuItem> = [
    {label: 'Ana Sayfa', icon: 'pi pi-home'},
    {label: 'Müşteriler', icon: 'pi pi-users'},
    {label: 'Ayarlar', icon: 'pi pi-cog'}
  ]
}