package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.List;

public class RangeValue extends GearshiftValue {

    protected int start, end, step;

    public RangeValue(int start, int end) {
        this.start = start;
        this.end = end;
        this.step = 1;
        declareFields();
    }
    private void declareFields() {
        declareField("__iterator", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Range.__iterator", args, 0, file, line);
                return new FunctionValue() {
                    int current = start;
                    @Override
                    public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                        checkArgs("Range.__iterator()", args, 0, file, line);
                        var ret = Integer.valueOf(current);
                        current += step;
                        if (ret > end)
                            return new NullValue();
                        return new NumberValue(ret);
                    }
                };
            }
        });
        declareField("__tostring", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Range.__tostring", args, 0, file, line);
                return new StringValue("from " + start + " to " + end + (step != 1 ? " step " + step : ""));
            }
        });
    }
    public RangeValue step(int step) {
        this.step = step;
        return this;
    }
    @Override
    public String getTypeName() {
        return "Range";
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(start) ^ Integer.hashCode(end) ^ Integer.hashCode(step);
    }
}
