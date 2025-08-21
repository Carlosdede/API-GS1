package br.com.akrus.gs1;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import java.math.BigDecimal;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;



public class ProdutoGS1 {


    public String montarJsonProduto(ContextoAcao contexto) throws Exception {
        List<String> camposVazios = new ArrayList<>();

        try {
            Registro[] registros = contexto.getLinhas();
            if (registros == null || registros.length == 0) {
                throw new Exception("Nenhum produto selecionado");
            }

            // PEGA APENAS O PRIMEIRO REGISTRO
            Registro registro = registros[0];

            // AGORA CAPTURE OS CAMPOS
            BigDecimal codProd = (BigDecimal) registro.getCampo("CODPROD");
            if (codProd == null) camposVazios.add("CODPROD");

            String descProd = (String) registro.getCampo("DESCRPROD");
            if (descProd == null || descProd.trim().isEmpty()) camposVazios.add("DESCRPROD");

            String marca = (String) registro.getCampo("MARCA");
            if (marca == null || marca.trim().isEmpty()) camposVazios.add("MARCA");

            String ncm = (String) registro.getCampo("NCM");
            if (ncm == null || ncm.trim().isEmpty()) camposVazios.add("NCM");

            BigDecimal pesoBruto = (BigDecimal) registro.getCampo("PESOBRUTO");
            if (pesoBruto == null) camposVazios.add("PESOBRUTO");

            BigDecimal pesoLiq = (BigDecimal) registro.getCampo("PESOLIQ");
            if (pesoLiq == null) camposVazios.add("PESOLIQ");

            String codVol = (String) registro.getCampo("CODVOL");
            if (codVol == null || codVol.trim().isEmpty()) camposVazios.add("CODVOL");

            BigDecimal altura = (BigDecimal) registro.getCampo("ALTURA");
            if (altura == null) camposVazios.add("ALTURA");

            BigDecimal largura = (BigDecimal) registro.getCampo("LARGURA");
            if (largura == null) camposVazios.add("LARGURA");

            BigDecimal profundidade = (BigDecimal) registro.getCampo("ESPESSURA");
            if (profundidade == null) camposVazios.add("ESPESSURA");

            BigDecimal quantidadeMinima = (BigDecimal) registro.getCampo("AD_QTDMINIMA");
            if (quantidadeMinima == null) camposVazios.add("AD_QTDMINIMA");

            // VERIFICA CAMPOS VAZIOS
            if (!camposVazios.isEmpty()) {
                String mensagemErro = camposVazios.size() == 1 ?
                        "Campo obrigatório não preenchido: " + camposVazios.get(0) :
                        "Campos obrigatórios não preenchidos: " + String.join(", ", camposVazios);

                contexto.setMensagemRetorno(mensagemErro);
                throw new Exception(mensagemErro);
            }

            Integer multiplo = 1;
            String cad = "A63862";
            String unidadeMedidaPesoBruto = "KGM";
            String unidadeMedidaConteudo = "MLT";
            String gpc = "10003291";
            String imagem = "";
            String cest = "";



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
                        .put("additionalTradeItemIdentificationValue", codProd.toString()));
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


