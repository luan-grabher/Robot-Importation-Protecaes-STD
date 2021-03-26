package robot.importation.protecaes.std;

import Entity.Executavel;
import Entity.Warning;
import TemplateContabil.Model.Entity.Importation;
import TemplateContabil.Model.Entity.LctoTemplate;
import TemplateContabil.Model.ImportationModel;
import fileManager.StringFilter;
import java.util.ArrayList;
import java.util.List;

public class Compare extends Executavel {

    private final Importation contasPagar;
    private final Importation extrato;
    private final Importation pgtosMensal;
    private List<LctoTemplate> compareLctos;

    public Compare(Importation contasPagar, Importation extrato, Importation pgtosMensal) {
        this.contasPagar = contasPagar;
        this.extrato = extrato;
        this.pgtosMensal = pgtosMensal;
    }

    @Override
    public void run() {
        /*Pega Lctos dos 3*/
        //ImportationModel.getLctosFromFile(contasPagar);
        //ImportationModel.getLctosFromFile(extrato);

        /*Somente o pgtos mensal não passou pelo importation model que define os lctos*/
        ImportationModel.getLctosFromFile(pgtosMensal);

        /*Arruma Extrato*/
        filtrarExtratoLctos();

        compareLctos = new ArrayList<>();
        compareLctos.addAll(extrato.getLctos());
        compareLctos.addAll(pgtosMensal.getLctos());

        throw new Warning(tableDiference());
    }

    /**
     * Retorna uma tabela em html com as diferencas
     */
    private String tableDiference() {
        StringBuilder html = new StringBuilder();

        StringBuilder rows = new StringBuilder();
        StringBuilder header = new StringBuilder();

        header.append(td("<b>Data</b>"));
        header.append(td("<b>Doc</b>"));
        header.append(td("<b>Historicos</b>"));
        header.append(td("<b>filial</b>"));
        header.append(td("<b>Vaslor</b>"));
        header.append(td("<b>E-S</b>"));

        rows.append(header);

        //Percorre todos compareLctos
        compareLctos.forEach((c) -> {
            //Se o valor não existir no contas a pagar
            if (!existsValue(contasPagar.getLctos(), c)) {
                StringBuilder row = new StringBuilder();

                row.append(tr(c.getData()));
                row.append(tr(c.getDocumento()));
                row.append(tr(c.getHistorico()));
                row.append(tr(""));
                row.append(tr(c.getValor().toPlainString()));
                row.append(tr(c.getEntrada_Saida()));

                rows.append(row);
            }
        });

        html.append(table(rows.toString()));

        return html.toString();
    }

    /**
     * @return Return if exists an lcto with the value in this date
     */
    private Boolean existsValue(List<LctoTemplate> lctos, LctoTemplate search) {
        try {
            lctos.forEach((lcto) -> {
                if (lcto.getData().equals(search.getData()) && lcto.getValor().compareTo(search.getValor()) == 0) {
                    throw new Error("");
                }
            });
        } catch (Error e) {
            return true;
        }

        return false;
    }

    /**
     * Filtra somente as tarifas e salarios
     *
     * @return Lista com tarifas e salarios
     */
    private List<LctoTemplate> filtrarExtratoLctos() {
        List<LctoTemplate> toRemove = new ArrayList<>();

        List<StringFilter> filters = new ArrayList<>();
        filters.add(new StringFilter("TAR;EMISSAO"));
        filters.add(new StringFilter("PAGAMENTO;SALARIO"));

        extrato.getLctos().forEach((lcto) -> {
            Boolean[] remove = new Boolean[]{true};

            filters.forEach((filter) -> {
                if (filter.filterOfString(lcto.getHistorico())) {
                    remove[0] = false;
                }
            });

            if (remove[0]) {
                toRemove.add(lcto);
            }
        });

        extrato.getLctos().removeAll(toRemove);

        return extrato.getLctos();
    }

    private static String table(String trs) {
        return "<table border='1'>" + trs + "</table>";
    }

    private static String tr(String td) {
        return "<tr>" + td + "</tr>";
    }

    private static String td(String html) {
        return "<td>" + html + "</td>";
    }

    private static String br() {
        return "<br>";
    }
}
