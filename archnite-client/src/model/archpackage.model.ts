export interface ArchPackage {
  id: number;
  architecture: string | null;
  packageName: string;
  description: string;
  lastUpdate: string;
  url: string;
  aur: boolean;
}
