package arch

import (
	"testing"
)

func TestLoadPageResponse(t *testing.T) {
	res, err := loadPageResponse(1)
	if len(res.Results) == 0 || err != nil {
		t.Fatalf(`loadPageResponse(1) failed: ResultsLen=%d, err=%v, want ResultsLen=250, err=nil`, len(res.Results), err)
	}
}

func TestLoadPageResponseLowerBound(t *testing.T) {
	res, err := loadPageResponse(-1)
	if len(res.Results) != 0 || err == nil {
		t.Fatalf(`loadPageResponse(-1) failed: ResultsLen=%d, err=%v, want ResultsLen=0, err=error`, len(res.Results), err)
	}
}

func TestLoadPageResponseUpperBound(t *testing.T) {
	res, err := loadPageResponse(1000)
	if len(res.Results) != 0 || err == nil {
		t.Fatalf(`loadPageResponse(1000) failed: ResultsLen=%d, err=%v, want ResultsLen=0, err=error`, len(res.Results), err)
	}
}

func TestLoadPackages(t *testing.T) {
	res, err := loadPackages()
	if len(res) < 14000 || err != nil {
		t.Fatalf(`loadPackages() failed: ResultsLen=%d, err=%v, want ResultsLen>14000, err=nil`, len(res), err)
	}
}

func BenchmarkLoadPackages(b *testing.B) {
	loadPackages()
}
