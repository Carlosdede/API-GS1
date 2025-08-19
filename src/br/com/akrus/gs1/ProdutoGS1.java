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
            try {
                // Captura os campos do produto vindos do Sankhya
                String codProd = (String) contexto.getParam("CODPROD");
                String descProd = (String) contexto.getParam("DESCRPROD");
                String cad = "A63862";
                String marca = (String) contexto.getParam("MARCA");
                String ncm = (String) contexto.getParam("NCM");
                String cest = (String) contexto.getParam("CEST");
                BigDecimal pesoBruto = (BigDecimal) contexto.getParam("PESOBRUTO");
                BigDecimal pesoLiq = (BigDecimal) contexto.getParam("PESOLIQ");
                String unidadeMedidaPesoBruto = "KGM"; // exemplo: Quilograma
                String unidadeMedidaConteudo = "MLT"; // exemplo: Mililitro
                String codVol = (String) contexto.getParam("CODVOL");
                String gpc = "10003291";
                BigDecimal altura = (BigDecimal) contexto.getParam("ALTURA");
                BigDecimal largura = (BigDecimal) contexto.getParam("LARGURA");
                BigDecimal profundidade = (BigDecimal) contexto.getParam("ESPESSURA");
                Integer quantidadeMinima = (Integer) contexto.getParam("QTDMINIMA");
                Integer multiplo = (Integer) contexto.getParam("MULTIPLO");
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


