package com.vfi.android.emvkernel.database;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbManager implements IDbOperation {
    private String dbRootPath;
    private Map<String, List<String>> emvAppParamsMap;
    private Map<String, List<String>> emvKeyParamsMap;

    private static class SingletonHolder {
        private static final DbManager INSTANCE = new DbManager();
    }

    private DbManager() {
        emvAppParamsMap = new HashMap<>();
        emvKeyParamsMap = new HashMap<>();
    }

    public static DbManager getInstance(String dbRootPath) {
        DbManager dbManager = SingletonHolder.INSTANCE;
        if (dbManager.getDbRootPath() == null) {
            dbManager.setDbRootPath(dbRootPath);
            dbRootPath = "/sdcard/emv_param";
            File file = new File(dbRootPath);
            if (file.exists()) {
                if (!file.isDirectory()) {
                    file.delete();
                    file.mkdir();
                }
            } else {
                file.mkdir();
            }
        } else if (!dbManager.getDbRootPath().equals(dbRootPath)) {
            // change db path
        }

        return SingletonHolder.INSTANCE;
    }

    public String getDbRootPath() {
        return dbRootPath;
    }

    public void setDbRootPath(String dbRootPath) {
        this.dbRootPath = dbRootPath;
    }

    @Override
    public void saveEmvAppParamList(int groupId, List<String> emvAppList) {

    }

    @Override
    public List<String> getEmvAppParamList(int groupId) {
        List<String> appParamList = new ArrayList<>();
        String terminalCap = "9F3303E0F8C8"; // support SDA DDA CDA
        String terminalCap1 = "9F3303E0F8A0"; // support SDA CDA
        String terminalCap2 = "9F3303E0F840"; // support DDA
        String terminalParameters = terminalCap2;
        appParamList.add("9F0608A000000003101001" + "DF010100" + terminalParameters + "9F09020200" + "9F350122");
        appParamList.add("9F0605A000000003" + "DF010100" + terminalParameters + "9F09020200" + "9F350122");
        appParamList.add("9F0605A000000004" + "DF010100" + terminalParameters + "9F09020002" + "9F350122");
        appParamList.add("9F0605A000000333" + "DF010100" + terminalParameters + "9F09020030" + "9F350122");
        appParamList.add("9F0608A000000333010102" + "DF010100" + terminalParameters + "9F09020030" + "9F350122");
        appParamList.add("9F0605A000000677" + "DF010100" + terminalParameters + "9F09020200" + "9F350122");
        appParamList.add("9F0605A000000025" + "DF010100" + terminalParameters + "9F09020001" + "9F350122");

        return appParamList;
    }

    @Override
    public void saveEmvKeyParamList(int groupId, List<String> emvKeyList) {

    }

    @Override
    public List<String> getEmvKeyParamList(int groupId) {
        List<String> appCapksList = new ArrayList<>();
        /**
         *                  case "Index":
         *                     return "9F22";
         *                 case "RID":
         *                     return "9F06";
         *                 case "Exponent":
         *                     return "DF04";
         *                 case "KeyLen":
         *                     return "";
         *                 case "Key":
         *                     return "DF0281";
         *                 case "Hash":
         *                     return "DF03";
         *                 case "HashAlgoIndicator":
         *                     return "DF06";
         *                 case "PKAlgoIndicator":
         *                     return "DF07";
         *                 case "ExpiryDate":
         *                     return "DF05";
         */
        // ===============AID==================AuthorKeyIndex==Exponent==keyLen=========key=======hash===hashAlgoIndicator===pubkeyAlgoIndicator===expireDate
        appCapksList.add("9F0605A000000003" + "9F220195" + "DF040103" + "DF0281"+"90"+"BE9E1FA5E9A803852999C4AB432DB28600DCD9DAB76DFAAA47355A0FE37B1508AC6BF38860D3C6C2E5B12A3CAAF2A7005A7241EBAA7771112C74CF9A0634652FBCA0E5980C54A64761EA101A114E0F0B5572ADD57D010B7C9C887E104CA4EE1272DA66D997B9A90B5A6D624AB6C57E73C8F919000EB5F684898EF8C3DBEFB330C62660BED88EA78E909AFF05F6DA627B" + "DF0314EE1511CEC71020A9B90443B37B1D5F6E703030F6" + "DF060101" + "DF070101" + "DF0503311217");
        appCapksList.add("9F0605A000000003" + "9F220192" + "DF040103" + "DF0281"+"B0"+"996AF56F569187D09293C14810450ED8EE3357397B18A2458EFAA92DA3B6DF6514EC060195318FD43BE9B8F0CC669E3F844057CBDDF8BDA191BB64473BC8DC9A730DB8F6B4EDE3924186FFD9B8C7735789C23A36BA0B8AF65372EB57EA5D89E7D14E9C7B6B557460F10885DA16AC923F15AF3758F0F03EBD3C5C2C949CBA306DB44E6A2C076C5F67E281D7EF56785DC4D75945E491F01918800A9E2DC66F60080566CE0DAF8D17EAD46AD8E30A247C9F" + "DF03"+"14"+"429C954A3859CEF91295F663C963E582ED6EB253" + "DF060101" + "DF070101" + "DF0503311217");
        appCapksList.add("9F0605A000000003" + "9F220194" + "DF040103" + "DF0281"+"F8"+"ACD2B12302EE644F3F835ABD1FC7A6F62CCE48FFEC622AA8EF062BEF6FB8BA8BC68BBF6AB5870EED579BC3973E121303D34841A796D6DCBC41DBF9E52C4609795C0CCF7EE86FA1D5CB041071ED2C51D2202F63F1156C58A92D38BC60BDF424E1776E2BC9648078A03B36FB554375FC53D57C73F5160EA59F3AFC5398EC7B67758D65C9BFF7828B6B82D4BE124A416AB7301914311EA462C19F771F31B3B57336000DFF732D3B83DE07052D730354D297BEC72871DCCF0E193F171ABA27EE464C6A97690943D59BDABB2A27EB71CEEBDAFA1176046478FD62FEC452D5CA393296530AA3F41927ADFE434A2DF2AE3054F8840657A26E0FC617" + "DF03"+"14"+"C4A3C43CCF87327D136B804160E47D43B60E6E0F" + "DF060101" + "DF070101" + "DF0503311217");
        appCapksList.add("9F0605A000000003" + "9F220109" + "DF040103" + "DF0281"+"F8"+"9D912248DE0A4E39C1A7DDE3F6D2588992C1A4095AFBD1824D1BA74847F2BC4926D2EFD904B4B54954CD189A54C5D1179654F8F9B0D2AB5F0357EB642FEDA95D3912C6576945FAB897E7062CAA44A4AA06B8FE6E3DBA18AF6AE3738E30429EE9BE03427C9D64F695FA8CAB4BFE376853EA34AD1D76BFCAD15908C077FFE6DC5521ECEF5D278A96E26F57359FFAEDA19434B937F1AD999DC5C41EB11935B44C18100E857F431A4A5A6BB65114F174C2D7B59FDF237D6BB1DD0916E644D709DED56481477C75D95CDD68254615F7740EC07F330AC5D67BCD75BF23D28A140826C026DBDE971A37CD3EF9B8DF644AC385010501EFC6509D7A41" + "DF03"+"14"+"1FF80A40173F52D7D27E0F26A146A1C8CCB29046" + "DF060101" + "DF070101" + "DF0503311217");
        appCapksList.add("9F0605A000000003" + "9F220108" + "DF040103" + "DF0281"+"B0"+"D9FD6ED75D51D0E30664BD157023EAA1FFA871E4DA65672B863D255E81E137A51DE4F72BCC9E44ACE12127F87E263D3AF9DD9CF35CA4A7B01E907000BA85D24954C2FCA3074825DDD4C0C8F186CB020F683E02F2DEAD3969133F06F7845166ACEB57CA0FC2603445469811D293BFEFBAFAB57631B3DD91E796BF850A25012F1AE38F05AA5C4D6D03B1DC2E568612785938BBC9B3CD3A910C1DA55A5A9218ACE0F7A21287752682F15832A678D6E1ED0B" + "DF03"+"14"+"20D213126955DE205ADC2FD2822BD22DE21CF9A8" + "DF060101" + "DF070101" + "DF0503311217");
        appCapksList.add("9F0605A000000003" + "9F220107" + "DF040103" + "DF0281"+"90"+"A89F25A56FA6DA258C8CA8B40427D927B4A1EB4D7EA326BBB12F97DED70AE5E4480FC9C5E8A972177110A1CC318D06D2F8F5C4844AC5FA79A4DC470BB11ED635699C17081B90F1B984F12E92C1C529276D8AF8EC7F28492097D8CD5BECEA16FE4088F6CFAB4A1B42328A1B996F9278B0B7E3311CA5EF856C2F888474B83612A82E4E00D0CD4069A6783140433D50725F" + "DF03"+"14"+"B4BC56CC4E88324932CBC643D6898F6FE593B172" + "DF060101" + "DF070101" + "DF0503311217");
        appCapksList.add("9F0605A000000003" + "9F220105" + "DF040103" + "DF0281"+"60"+"D0135CE8A4436C7F9D5CC66547E30EA402F98105B71722E24BC08DCC80AB7E71EC23B8CE6A1DC6AC2A8CF55543D74A8AE7B388F9B174B7F0D756C22CBB5974F9016A56B601CCA64C71F04B78E86C501B193A5556D5389ECE4DEA258AB97F52A3" + "DF03"+"14"+"86DF041E7995023552A79E2623E49180C0CD957A" + "DF060101" + "DF070101" + "DF0503311217");

        // UPI Card
        appCapksList.add("9F0605A000000333" + "9F220108" + "DF040103" + "DF0281"+"90"+"B61645EDFD5498FB246444037A0FA18C0F101EBD8EFA54573CE6E6A7FBF63ED21D66340852B0211CF5EEF6A1CD989F66AF21A8EB19DBD8DBC3706D135363A0D683D046304F5A836BC1BC632821AFE7A2F75DA3C50AC74C545A754562204137169663CFCC0B06E67E2109EBA41BC67FF20CC8AC80D7B6EE1A95465B3B2657533EA56D92D539E5064360EA4850FED2D1BF" + "DF03"+"14"+"EE23B616C95C02652AD18860E48787C079E8E85A" + "DF060101" + "DF070101" + "DF0503311217");
        appCapksList.add("9F0605A000000333" + "9F220109" + "DF040103" + "DF0281"+"B0"+"EB374DFC5A96B71D2863875EDA2EAFB96B1B439D3ECE0B1826A2672EEEFA7990286776F8BD989A15141A75C384DFC14FEF9243AAB32707659BE9E4797A247C2F0B6D99372F384AF62FE23BC54BCDC57A9ACD1D5585C303F201EF4E8B806AFB809DB1A3DB1CD112AC884F164A67B99C7D6E5A8A6DF1D3CAE6D7ED3D5BE725B2DE4ADE23FA679BF4EB15A93D8A6E29C7FFA1A70DE2E54F593D908A3BF9EBBD760BBFDC8DB8B54497E6C5BE0E4A4DAC29E5" + "DF03"+"14"+"A075306EAB0045BAF72CDD33B3B678779DE1F527" + "DF060101" + "DF070101" + "DF0503311217");
        appCapksList.add("9F0605A000000333" + "9F22010B" + "DF040103" + "DF0281"+"F8"+"CF9FDF46B356378E9AF311B0F981B21A1F22F250FB11F55C958709E3C7241918293483289EAE688A094C02C344E2999F315A72841F489E24B1BA0056CFAB3B479D0E826452375DCDBB67E97EC2AA66F4601D774FEAEF775ACCC621BFEB65FB0053FC5F392AA5E1D4C41A4DE9FFDFDF1327C4BB874F1F63A599EE3902FE95E729FD78D4234DC7E6CF1ABABAA3F6DB29B7F05D1D901D2E76A606A8CBFFFFECBD918FA2D278BDB43B0434F5D45134BE1C2781D157D501FF43E5F1C470967CD57CE53B64D82974C8275937C5D8502A1252A8A5D6088A259B694F98648D9AF2CB0EFD9D943C69F896D49FA39702162ACB5AF29B90BADE005BC157" + "DF03"+"14"+"BD331F9996A490B33C13441066A09AD3FEB5F66C" + "DF060101" + "DF070101" + "DF0503311217");
        appCapksList.add("9F0605A000000333" + "9F220104" + "DF040103" + "DF0281"+"F8"+"BC853E6B5365E89E7EE9317C94B02D0ABB0DBD91C05A224A2554AA29ED9FCB9D86EB9CCBB322A57811F86188AAC7351C72BD9EF196C5A01ACEF7A4EB0D2AD63D9E6AC2E7836547CB1595C68BCBAFD0F6728760F3A7CA7B97301B7E0220184EFC4F653008D93CE098C0D93B45201096D1ADFF4CF1F9FC02AF759DA27CD6DFD6D789B099F16F378B6100334E63F3D35F3251A5EC78693731F5233519CDB380F5AB8C0F02728E91D469ABD0EAE0D93B1CC66CE127B29C7D77441A49D09FCA5D6D9762FC74C31BB506C8BAE3C79AD6C2578775B95956B5370D1D0519E37906B384736233251E8F09AD79DFBE2C6ABFADAC8E4D8624318C27DAF1" + "DF03"+"14"+"F527081CF371DD7E1FD4FA414A665036E0F5E6E5" + "DF060101" + "DF070101" + "DF0503241231");

        // AMEX Card
//        appCapksList.add("9F0605A000000333" + "9F220104" + "DF040103" + "DF0281"+"F8"+"BC853E6B5365E89E7EE9317C94B02D0ABB0DBD91C05A224A2554AA29ED9FCB9D86EB9CCBB322A57811F86188AAC7351C72BD9EF196C5A01ACEF7A4EB0D2AD63D9E6AC2E7836547CB1595C68BCBAFD0F6728760F3A7CA7B97301B7E0220184EFC4F653008D93CE098C0D93B45201096D1ADFF4CF1F9FC02AF759DA27CD6DFD6D789B099F16F378B6100334E63F3D35F3251A5EC78693731F5233519CDB380F5AB8C0F02728E91D469ABD0EAE0D93B1CC66CE127B29C7D77441A49D09FCA5D6D9762FC74C31BB506C8BAE3C79AD6C2578775B95956B5370D1D0519E37906B384736233251E8F09AD79DFBE2C6ABFADAC8E4D8624318C27DAF1" + "DF03"+"14"+"F527081CF371DD7E1FD4FA414A665036E0F5E6E5" + "DF060101" + "DF070101" + "DF0503241231");

        // Mastercard
        appCapksList.add("9F0605A000000004" + "9F2201F1" + "DF040103" + "DF0281"+"B0"+"A0DCF4BDE19C3546B4B6F0414D174DDE294AABBB828C5A834D73AAE27C99B0B053A90278007239B6459FF0BBCD7B4B9C6C50AC02CE91368DA1BD21AAEADBC65347337D89B68F5C99A09D05BE02DD1F8C5BA20E2F13FB2A27C41D3F85CAD5CF6668E75851EC66EDBF98851FD4E42C44C1D59F5984703B27D5B9F21B8FA0D93279FBBF69E090642909C9EA27F898959541AA6757F5F624104F6E1D3A9532F2A6E51515AEAD1B43B3D7835088A2FAFA7BE7" + "DF03"+"14"+"D8E68DA167AB5A85D8C3D55ECB9B0517A1A5B4BB" + "DF060101" + "DF070101" + "DF0503241231");
        appCapksList.add("9F0605A000000004" + "9F220104" + "DF040103" + "DF0281"+"90"+"A6DA428387A502D7DDFB7A74D3F412BE762627197B25435B7A81716A700157DDD06F7CC99D6CA28C2470527E2C03616B9C59217357C2674F583B3BA5C7DCF2838692D023E3562420B4615C439CA97C44DC9A249CFCE7B3BFB22F68228C3AF13329AA4A613CF8DD853502373D62E49AB256D2BC17120E54AEDCED6D96A4287ACC5C04677D4A5A320DB8BEE2F775E5FEC5" + "DF03"+"14"+"381A035DA58B482EE2AF75F4C3F2CA469BA4AA6C" + "DF060101" + "DF070101" + "DF0503241231");
        appCapksList.add("9F0605A000000004" + "9F220106" + "DF040103" + "DF0281"+"F8"+"CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747F" + "DF03"+"14"+"F910A1504D5FFB793D94F3B500765E1ABCAD72D9" + "DF060101" + "DF070101" + "DF0503241231");


        return appCapksList;
    }
}
