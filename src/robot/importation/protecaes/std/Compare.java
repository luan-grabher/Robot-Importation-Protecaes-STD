
package robot.importation.protecaes.std;

import Entity.Executavel;
import TemplateContabil.Model.Entity.Importation;
import TemplateContabil.Model.Entity.LctoTemplate;
import TemplateContabil.Model.ImportationModel;
import fileManager.StringFilter;
import java.util.ArrayList;
import java.util.List;


public class Compare extends Executavel{
    private Importation contasPagar;
    private Importation extrato;
    private Importation pgtosMensal;

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
        
        filtrarExtratoLctos();
        
        List<LctoTemplate> compareLctos = new ArrayList<>();
        compareLctos.addAll(extrato.getLctos());
        compareLctos.addAll(pgtosMensal.getLctos());
        
        
        /*Arruma Extrato*/
    }
    
    /**
     * Filtra somente as tarifas e salarios
     * 
     * @return Lista com tarifas e salarios
     */
    private List<LctoTemplate> filtrarExtratoLctos(){
        List<LctoTemplate> toRemove =  new ArrayList<>();
        
        List<StringFilter> filters  = new ArrayList<>();
        filters.add(new StringFilter("TAR;EMISSAO"));
        filters.add(new StringFilter("PAGAMENTO;SALARIO"));
        
        
        extrato.getLctos().forEach((lcto) ->{
            Boolean[] remove = new Boolean[]{true};
            
            filters.forEach((filter)->{
                if(filter.filterOfString(lcto.getHistorico())){
                    remove[0] = false;
                }
            });
            
            if(remove[0]){
                toRemove.add(lcto);
            }
        });
        
        extrato.getLctos().removeAll(toRemove);
        
        return extrato.getLctos();
    }
    
}
