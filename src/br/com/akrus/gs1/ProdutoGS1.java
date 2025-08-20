package br.com.akrus.gs1;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.*;
import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONObject;



public class ProdutoGS1 {


    public String montarJsonProduto(ContextoAcao contexto) throws Exception {
        List<String> camposVazios = new ArrayList<>();

        try {
/*
            Object codProdParam = contexto.getParam("CODPROD");
            if (codProdParam == null) camposVazios.add("CODPROD");
            int codProd = codProdParam != null ?
                    (codProdParam instanceof Integer ? (Integer) codProdParam :
                            codProdParam instanceof BigDecimal ? ((BigDecimal) codProdParam).intValue() :
                                    Integer.parseInt(codProdParam.toString())) : 0;

 */
            Integer codProd = (Integer) contexto.getParam("CODPROD");
            if (codProd == null) camposVazios.add("CODPROD");

            String descProd = (String) contexto.getParam("DESCRPROD");
            if (descProd == null || descProd.trim().isEmpty()) camposVazios.add("DESCRPROD");


            String marca = (String) contexto.getParam("MARCA");
            if (marca == null || marca.trim().isEmpty()) camposVazios.add("MARCA");

            String ncm = (String) contexto.getParam("NCM");
            if (ncm == null || ncm.trim().isEmpty()) camposVazios.add("NCM");

            String cest = (String) contexto.getParam("CEST");
            // CEST pode ser opcional, então não adicionamos na lista de erros

            BigDecimal pesoBruto = (BigDecimal) contexto.getParam("PESOBRUTO");
            if (pesoBruto == null) camposVazios.add("PESOBRUTO");

            BigDecimal pesoLiq = (BigDecimal) contexto.getParam("PESOLIQ");
            if (pesoLiq == null) camposVazios.add("PESOLIQ");

            String codVol = (String) contexto.getParam("CODVOL");
            if (codVol == null || codVol.trim().isEmpty()) camposVazios.add("CODVOL");

            BigDecimal altura = (BigDecimal) contexto.getParam("ALTURA");
            if (altura == null) camposVazios.add("ALTURA");

            BigDecimal largura = (BigDecimal) contexto.getParam("LARGURA");
            if (largura == null) camposVazios.add("LARGURA");

            BigDecimal profundidade = (BigDecimal) contexto.getParam("ESPESSURA");
            if (profundidade == null) camposVazios.add("ESPESSURA");

            Integer quantidadeMinima = (Integer) contexto.getParam("QTDMINIMA");
            if (quantidadeMinima == null) camposVazios.add("QTDMINIMA");

            Integer multiplo = (Integer) contexto.getParam("MULTIPLO");
            if (multiplo == null) camposVazios.add("MULTIPLO");


            if (!camposVazios.isEmpty()) {
                String mensagemErro;
                if (camposVazios.size() == 1) {
                    mensagemErro = "Campo obrigatório não preenchido: " + camposVazios.get(0);
                } else {
                    mensagemErro = "Campos obrigatórios não preenchidos: " + String.join(", ", camposVazios);
                }
                contexto.setMensagemRetorno(mensagemErro);
                throw new Exception(mensagemErro);
            }


            String cad = "A63862";
            String unidadeMedidaPesoBruto = "KGM";
            String unidadeMedidaConteudo = "MLT";
            String gpc = "10003291";
            String imagem = "";



                switch (marca) {
                    case "SAFRAMIX":
                        imagem = "https://raw.githubusercontent.com/dpti-akrus/imagensGS1/main/Saframix.jpg";
                        break;
                    case "INTEGRACAO":
                        imagem = "https://raw.githubusercontent.com/dpti-akrus/imagensGS1/main/Integracao.jpg";
                        break;
                    case "DAVANTTI":
                        imagem = "https://raw.githubusercontent.com/dpti-akrus/imagensGS1/main/Davanttii.jpg";
                        break;
                    case "GSM":
                        imagem = "https://raw.githubusercontent.com/dpti-akrus/imagensGS1/main/GSM.jpg";
                        break;
                    default:
                        imagem = "https://raw.githubusercontent.com/dpti-akrus/imagensGS1/main/Safrasul.jpg";;
                        break;
                }

                System.out.println(marca);


                // Monta o JSON na estrutura GS1
                JSONObject produtoJson = new JSONObject();

// Company
                produtoJson.put("company", new JSONObject()
                        .put("cad",cad));

// Status
                produtoJson.put("gtinStatusCode", "ACTIVE");

// Descrição
                JSONArray descricoes = new JSONArray();
                descricoes.put(new JSONObject()
                        .put("tradeItemDescription", descProd)
                        .put("languageCode", "pt-BR")
                        .put("default", true));
                produtoJson.put("tradeItemDescriptionInformationLang", descricoes);

// Identificação adicional (SKU)
                JSONArray additionalIds = new JSONArray();
                additionalIds.put(new JSONObject()
                        .put("additionalTradeItemIdentificationTypeCode", "SKU")
                        .put("additionalTradeItemIdentificationValue", codProd));
                produtoJson.put("additionalTradeItemIdentifications", additionalIds);

// Identificação GS1
                produtoJson.put("gs1TradeItemIdentificationKey", new JSONObject()
                        .put("gs1TradeItemIdentificationKeyCode", "GTIN_13"));

// Imagens

                    JSONArray imagens = new JSONArray();
                    imagens.put(new JSONObject()
                            .put("uniformResourceIdentifier", imagem)
                            .put("referencedFileTypeCode", "PLANOGRAM")
                            .put("featuredFile", true));
                    produtoJson.put("referencedFileInformations", imagens);


// Medidas
                JSONObject medidas = new JSONObject()
                        .put("height", new JSONObject()
                                .put("measurementUnitCode", "CMT")
                                .put("value", altura))
                        .put("width", new JSONObject()
                                .put("measurementUnitCode", "CMT")
                                .put("value", largura))
                        .put("depth", new JSONObject()
                                .put("measurementUnitCode", "CMT")
                                .put("value", profundidade))
                        .put("netContent", new JSONObject()
                                .put("measurementUnitCode", "GRM")
                                .put("value", 1));
                produtoJson.put("tradeItemMeasurements", medidas);

// Marca
                JSONArray marcas = new JSONArray();
                marcas.put(new JSONObject()
                        .put("brandName", marca)
                        .put("languageCode", "pt-BR")
                        .put("default", true));
                produtoJson.put("brandNameInformationLang", marcas);

// Peso
                produtoJson.put("tradeItemWeight", new JSONObject()
                        .put("grossWeight", new JSONObject()
                                .put("value", pesoBruto)
                                .put("measurementUnitCode", "KGM"))
                        .put("netWeight", new JSONObject()
                                .put("value", pesoLiq)
                                .put("measurementUnitCode", "KGM")));

// Classificação
                JSONArray classificacoes = new JSONArray();
                classificacoes.put(new JSONObject()
                        .put("additionalTradeItemClassificationSystemCode", "NCM")
                        .put("additionalTradeItemClassificationCodeValue", ncm));

                produtoJson.put("tradeItemClassification", new JSONObject()
                        .put("additionalTradeItemClassifications", classificacoes)
                        .put("gpcCategoryCode", gpc));

// Trade Item
                produtoJson.put("tradeItem", new JSONObject()
                        .put("targetMarket", new JSONObject()
                                .put("targetMarketCountryCodes", new JSONArray().put("076")))
                        .put("tradeItemUnitDescriptorCode", "BASE_UNIT_OR_EACH"));

// Origem
                produtoJson.put("placeOfProductActivity", new JSONObject()
                        .put("countryOfOrigin", new JSONObject()
                                .put("countryCode", "076")
                                .put("countrySubdivisionCodes", new JSONArray().put("BR-MG"))));

// Informações de compra
                produtoJson.put("deliveryPurchasingInformation", new JSONObject()
                        .put("orderQuantityMinimum", quantidadeMinima)
                        .put("orderQuantityMultiple", multiplo)
                        .put("orderSizingFactor", "KG"));

// Flags
                produtoJson.put("inDevelopmentWithoutGrossWeight", false);
                produtoJson.put("withoutCest", true);
                produtoJson.put("acceptResponsibility", true);
                produtoJson.put("shareDataIndicator", true);

                return produtoJson.toString(4);


            } catch (Exception e) {
                throw new Exception("Erro ao montar JSON do produto: " + e.getMessage(), e);
            }
        }
    }


