import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ArchPackage } from "../model/archpackage.model";

@Injectable({ providedIn: 'root' })
export class ArchPackageService {
  private apiUrl = 'http://localhost:8080/packages/arch';

  constructor(private http: HttpClient) { }

  searchPackages(keyword: string, packageType: string): Observable<ArchPackage[]> {
    let packages: Observable<ArchPackage[]>
    switch (packageType) {
      case "Aur":
        packages = this.http.get<ArchPackage[]>(`${this.apiUrl}/search?keyword=${keyword}&aur=true`);
        break;
      case "Official":
        packages = this.http.get<ArchPackage[]>(`${this.apiUrl}/search?keyword=${keyword}&aur=false`);
        break;
      default:
        packages = this.http.get<ArchPackage[]>(`${this.apiUrl}/search?keyword=${keyword}`);
    }

    return packages;
  }
}
