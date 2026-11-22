package service;

public class StrictBlendRuleImpl implements CalibrationRule {

    @Override
    public int calculateBlendWidth(double rmsError, double threshold) {
        if (rmsError > threshold * 0.8) return 0;   // ужесточённый порог — 80%
        return (int) (48 + (threshold - rmsError) * 30);
    }

}