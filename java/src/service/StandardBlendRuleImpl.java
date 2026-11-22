package service;

public class StandardBlendRuleImpl implements CalibrationRule {

    @Override
    public int calculateBlendWidth(double rmsError, double threshold) {
        if (rmsError > threshold) return 0;
        return (int) (64 + (threshold - rmsError) * 20);
    }

}