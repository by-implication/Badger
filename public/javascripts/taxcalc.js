
function taxCalc (x) {
	if (x < 10000) {
		return 0.05 * (10000-x)
	} else if (x < 30000) {
		return 0.10 * (10000-x)
	} else if (x < 70000) {
		return 0.15 * (30000-x)
	} else if (x < 140000) {
		return 0.20 * (70000-x)
	} else if (x < 250000) {
		return 0.25 * (140000-x)
	} else if (x < 500000){
		return 0.30 * (250000-x)
	} else {
		return 0.34 * (500000-x)
	}
}