package teste;

import robot.importation.protecaes.std.RobotImportationProtecaesSTD;



public class teste {

    public static void main(String[] args) {
        StringBuilder parametros = new StringBuilder();
        
        parametros.append("[mes:3]");
        parametros.append("[ano:2021]");
        parametros.append("[ini:robot-protecaesSTD]");

        RobotImportationProtecaesSTD.testParameters = parametros.toString();
        args = new String[]{"test"};

        RobotImportationProtecaesSTD.main(args);
    }
}
