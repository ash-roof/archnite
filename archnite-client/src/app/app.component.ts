import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { RadioButtonModule } from 'primeng/radiobutton';
import { ArchPackage } from '../model/archpackage.model';
import { ArchPackageService } from '../service/archpackage.service';

interface AutoCompleteCompleteEvent {
  originalEvent: Event;
  query: string;
}

@Component({
  selector: 'app-root',
  imports: [ButtonModule, AutoCompleteModule, FormsModule, RadioButtonModule],
  templateUrl: './app.component.html',
})
export class AppComponent {
  selectedPackages: ArchPackage[];
  suggestedPackages: string[];
  packageType: string;

  constructor(private titleService: Title, private archPackageService: ArchPackageService) {
    this.titleService.setTitle("Archnite");
    this.suggestedPackages = [];
    this.selectedPackages = [];
    this.packageType = "All";
  }
  search(event: AutoCompleteCompleteEvent) {
    this.archPackageService.searchPackages(event.query, this.packageType).subscribe({
      next: (results: ArchPackage[]) => {
        this.suggestedPackages = results.map((archPackage) => archPackage.packageName);
      },
      error: (err) => {
        console.error('Package search failed', err);
        this.suggestedPackages = [];
      }
    });
  }
}
