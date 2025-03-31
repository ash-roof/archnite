import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { AutoCompleteModule } from 'primeng/autocomplete';

interface AutoCompleteCompleteEvent {
  originalEvent: Event;
  query: string;
}

@Component({
  selector: 'app-root',
  imports: [ButtonModule, AutoCompleteModule, FormsModule],
  templateUrl: './app.component.html',
})
export class AppComponent {
  selectedPackages: string[];
  suggestedPackages: string[];

  constructor(private titleService: Title) {
    this.titleService.setTitle("Archnite")
    this.suggestedPackages = []
    this.selectedPackages = []
  }
  search(event: AutoCompleteCompleteEvent) {
    this.suggestedPackages = [...Array(10).keys()].map((item) => event.query + '-' + item);
  }
}
