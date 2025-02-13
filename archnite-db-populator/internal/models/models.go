package models

import "time"

type ArchPackage struct {
	Name         string    `json:"pkgname"`
	Architecture string    `json:"arch"`
	Description  string    `json:"pkgdesc"`
	Url          string    `json:"url"`
	LastUpdate   time.Time `json:"last_update"`
}

type AurPackage struct {
	Name        string `json:"Name"`
	Description string `json:"Description"`
	Url         string `json:"URL"`
	LastUpdate  int64  `json:"LastModified"`
}

type ArchResponse struct {
	TotalPages int           `json:"num_pages"`
	Page       int           `json:"page"`
	Results    []ArchPackage `json:"results"`
}
