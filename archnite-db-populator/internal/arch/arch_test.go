package arch

import (
	"testing"
)

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

func BenchmarkLoadPackages(b *testing.B) {
	LoadPackages()
}
