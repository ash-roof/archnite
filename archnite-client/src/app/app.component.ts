import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { RadioButtonModule } from 'primeng/radiobutton';
import { ButtonModule } from 'primeng/button';

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
  suggestedPackages: ArchPackage[];
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
        this.suggestedPackages = results;
      },
      error: (err) => {
        console.error('Package search failed', err);
        this.suggestedPackages = [];
      }
    });
  }

  handleSuggestionLinkClick(pkg: ArchPackage, event: Event) {
    event.preventDefault();
    event.stopPropagation();
    window.open(pkg.url, '_blank');
  }

  copyToClipboard() {
    if (this.selectedPackages.length === 0) return;

    const packageNames = this.selectedPackages.map(pkg => pkg.packageName).join(' ');
    navigator.clipboard.writeText(packageNames)
      .then(() => {
        console.log('Copied to clipboard:', packageNames);
      })
      .catch(err => {
        console.error('Failed to copy:', err);
      });
  }
}
