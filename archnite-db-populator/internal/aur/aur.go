package aur

import (
	"encoding/json"
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

type UnixTimestamp time.Time

type AurPackage struct {
	Name        string        `json:"Name"`
	Description string        `json:"Description"`
	Url         string        `json:"URL"`
	LastUpdate  UnixTimestamp `json:"LastModified"`
}

func (t *UnixTimestamp) UnmarshalJSON(b []byte) error {
	var unixTime int64
	if err := json.Unmarshal(b, &unixTime); err != nil {
		return err
	}
	*t = UnixTimestamp(time.Unix(unixTime, 0).UTC())
	return nil
}

func DownloadFile(filepath string, url string) (err error) {
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

func loadAurPackages(path string) ([]AurPackage, error) {
	pkgDump, err := os.Open(path)
	if err != nil {
		return []AurPackage{}, fmt.Errorf("error reading %s: %w", path, err)
	}
	defer pkgDump.Close()

	var packages []AurPackage
	if err = json.NewDecoder(pkgDump).Decode(&packages); err != nil {
		return []AurPackage{}, fmt.Errorf("error parsing %s: %w", path, err)
	}

	return packages, nil
}
