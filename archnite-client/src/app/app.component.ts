import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { ButtonComponent } from './button/button.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ButtonComponent],
  templateUrl: './app.component.html',
})
export class AppComponent {
  constructor(private titleService: Title) {
    this.titleService.setTitle('archnite');
  }
}
