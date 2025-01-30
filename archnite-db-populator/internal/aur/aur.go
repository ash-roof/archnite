package aur

import (
	"fmt"
	"io"
	"net/http"
	"os"
	"time"
)

//"Name":"010editor",
//"PackageBaseID":116651,
//"PackageBase":"010editor",
//"Version":"15.0.1-1",
//"Description":"Professional text and hex editing with Binary Templates technology",
//"URL":"https://www.sweetscape.com/010editor/",
//"NumVotes":17,
//"Popularity":1.178722,
//"OutOfDate":null,
//"Maintainer":"Zrax",
//"Submitter":"ondrej",
//"FirstSubmitted":1477968580,
//"LastModified":1729276661,
//"URLPath":"/cgit/aur.git/snapshot/010editor.tar.gz",
//"Depends":["libpng"],
//"License":["custom"],
//"Keywords":["010","binary","hex","sweetscape"]

type AurPackage struct {
	Name        string    `json:"Name"`
	Description string    `json:"Description"`
	Url         string    `json:"URL"`
	LastUpdate  time.Time `json:"LastModified"`
}

func downloadFile(filepath string, url string) (err error) {
	file, err := os.Create(filepath)
	if err != nil {
		return fmt.Errorf("error creating file: %w", err)
	}
	defer file.Close()

	resp, err := http.Get(url)
	if err != nil {
		return fmt.Errorf("error fetching data: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("bad status: %s", resp.Status)
	}

	_, err = io.Copy(file, resp.Body)
	if err != nil {
		return fmt.Errorf("error copying data to file: %w", err)
	}

	return nil
}
