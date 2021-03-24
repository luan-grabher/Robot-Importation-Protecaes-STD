package robot.importation.protecaes.std;

import Entity.Executavel;
import JExcel.XLSX;
import Robo.AppRobo;
import TemplateContabil.Control.ControleTemplates;
import TemplateContabil.Model.Entity.Importation;
import fileManager.Args;
import fileManager.FileManager;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.ini4j.Ini;

public class RobotImportationProtecaesSTD {

    private static String nomeApp = "";

    public static void main(String[] args) {
        try {
            AppRobo robo = new AppRobo(nomeApp);
            robo.definirParametros();

            String iniPath = "\\\\heimerdinger\\docs\\Informatica\\Programas\\Moresco\\Robos\\Contabilidade\\TemplateImportacao\\";
            String iniName = robo.getParametro("ini");

            Ini ini = new Ini(FileManager.getFile(iniPath + iniName + ".ini"));

            String pastaEmpresa = ini.get("Pastas", "empresa");
            String pastaAnual = ini.get("Pastas", "anual");
            String pastaMensal = ini.get("Pastas", "mensal");

            int mes = Integer.valueOf(robo.getParametro("mes"));
            mes = mes >= 1 && mes <= 12 ? mes : 1;
            int ano = Integer.valueOf(robo.getParametro("ano"));

            nomeApp = "Importação " + pastaEmpresa + " - " + ini.get("Config", "nome") + " " + mes + "/" + ano;

            StringBuilder returnExecutions = new StringBuilder();

            String[] templates = ini.get("Config", "templates").split(";");
            //Para cada template pega as informações
            for (String template : templates) {
                template = !template.equals("") ? " " + template : "";

                String nomeTemplate = ini.get("Template" + template, "nome");
                String idTemplate = ini.get("Template" + template, "id");
                String filtroArquivo = ini.get("Template" + template, "filtroArquivo");
                String tipo = ini.get("Template" + template, "tipo");

                Map<String, Map<String, String>> colunas = new HashMap<>();
                if (tipo.equals("excel")) {
                    colunas.put("data", getCollumnConfig("data", template, ini));
                    colunas.put("documento", getCollumnConfig("documento", template, ini));
                    colunas.put("pretexto", getCollumnConfig("pretexto", template, ini));
                    colunas.put("historico", getCollumnConfig("historico",template, ini));
                    colunas.put("entrada", getCollumnConfig("entrada", template, ini));
                    colunas.put("saida", getCollumnConfig("saida", template, ini));
                    colunas.put("valor", getCollumnConfig("valor", template, ini));
                }

                returnExecutions.append("\n").append(
                        start(mes, ano, pastaEmpresa, pastaAnual, pastaMensal, nomeTemplate, idTemplate, filtroArquivo, tipo, colunas)
                );
            }

            robo.setNome(nomeApp);
            robo.executar(returnExecutions.toString());
        } catch (Exception e) {
            System.out.println("Ocorreu um erro na aplicação: " + e);
            System.exit(0);
        }
    }

    private static Map<String,String> getCollumnConfig(String collumnName, String template, Ini ini){
        return XLSX.getCollumnConfigFromString(collumnName, ini.get("Colunas" + template, collumnName));
    }


    public static String start(int mes, int ano, String pastaEmpresa, String pastaAnual, String pastaMensal, String nomeTemplate, String idTemplate, String filtroArquivo, String tipo, Map<String, Map<String, String>> colunas) {
        try {
            Importation importation = new Importation();
            importation.setTIPO(tipo.equals("excel") ? Importation.TIPO_EXCEL : Importation.TIPO_OFX);
            importation.getXlsxCols().putAll(colunas);

            importation.setIdTemplateConfig(idTemplate);
            importation.setNome(nomeTemplate);
            

            ControleTemplates controle = new ControleTemplates(mes, ano);
            controle.setPastaEscMensal(pastaEmpresa);
            controle.setPasta(pastaAnual, pastaMensal);

            Map<String, Executavel> execs = new LinkedHashMap<>();
            execs.put("Procurando arquivo " + nomeTemplate, controle.new defineArquivoNaImportacao(filtroArquivo, importation));
            execs.put("Criando template " + nomeTemplate, controle.new converterArquivoParaTemplate(importation));

            return AppRobo.rodarExecutaveis(nomeApp, execs);
        } catch (Exception e) {
            return "Ocorreu um erro no Java: " + e;
        }
    }

}
