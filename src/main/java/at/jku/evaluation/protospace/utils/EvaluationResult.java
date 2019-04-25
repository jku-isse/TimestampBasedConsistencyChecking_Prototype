package at.jku.evaluation.protospace.utils;

import java.math.BigDecimal;

public class EvaluationResult {
    private String name;
    private int modelElements;
    private int creationCommands;
    private int updateCommands;
    private int totalCommands;
    private int isolatedAccesses;
    private int integratedAccesses;
    private int totalAccesses;
    private int evaluations;
    private float meanTime;

    public EvaluationResult(String name, int modelElements, int creationCommands, int updateCommands, int isolatedAccesses, int integratedAccesses, int evaluations, float meanTime) {
        this.name = name;
        this.modelElements = modelElements;
        this.creationCommands = creationCommands;
        this.updateCommands = updateCommands;
        this.isolatedAccesses = isolatedAccesses;
        this.integratedAccesses = integratedAccesses;
        this.totalCommands = creationCommands + updateCommands;
        this.totalAccesses = isolatedAccesses+integratedAccesses;
        this.evaluations = evaluations;
        this.meanTime = meanTime;

    }

    @Override
    public String toString() {
        return "{Model Elements: "+modelElements+";   "+"Creation Commands: "+creationCommands+";   "+"Update Commands: "+updateCommands+";   "+"Total Commands: "+totalCommands+";   "
                +"Isolated Accesses: "+isolatedAccesses+";   "+"Integrated Accesses: "+integratedAccesses+";   "+"Evaluations: "+evaluations+"     name: "+name+"}";
    }

    public String toLatexTable(int index)
    {
        float intAccPer = (integratedAccesses*100)/((float)totalAccesses);
        float isoAccPer = (isolatedAccesses*100)/((float)totalAccesses);
        BigDecimal intAccPerBD = new BigDecimal(Float.toString(intAccPer));
        intAccPerBD = intAccPerBD.setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal isoAccPerBP = new BigDecimal(Float.toString(isoAccPer));
        isoAccPerBP = isoAccPerBP.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal meanTimeBD = new BigDecimal(Float.toString(meanTime));
        meanTimeBD = meanTimeBD.setScale(2, BigDecimal.ROUND_HALF_UP);

        return "PR0"+index+" & "+modelElements+" & "+totalCommands+" & "+creationCommands+" & "+updateCommands+" & "
            +evaluations+" & "+isolatedAccesses+" & "+integratedAccesses+" & "+isoAccPerBP+"& "
                +intAccPerBD+" & "+meanTimeBD+"  \\\\";
    }


}
