<!DOCTYPE html>
<html>
<head>
    <meta name="referrer" content="always" />
    <meta charset='utf-8' />
    <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
    <meta name="format-detection" content="telephone=no"/>
    <title>提款借据合同</title>
    <style>
        @page{
            size: A4;
        !''}

        body {
            padding: 16mm;
            line-height: 1.6;
            font-size: 14px;
            font-family: SimSun
        !''}
    </style>
</head>
<body style="padding: 20px;margin:0;background: #fff;font-size: 14px;color: #686868;font-family: SimSun;">
<p style="font-size:14px;">
    <p  align="center" style="text-align:center;">
    <p style="font-size:14px;">
        <span style="font-size:16px;font-weight:bold;">借款借据</span>
    </p>
    <p style="text-align:justify;font-size:14px;">
        <span style="">为了保护您的信息，本借款借据在电子服务平台展示时部分隐藏借款人的敏感信息，但本借款借据对借款人敏感信息的部分隐藏不影响本借款借据的效力，借款人知晓并同意本借款借据项下借款人的身份信息以借款人用于接受贷款的账户存留信息为准。</span>
    </p>
    <p style="text-align:justify;font-size:14px;">
        <span style="font-weight:bold;">您的本次借款借据如下：</span>
    </p>
    <p style="">
        <table width="100%" border="1" style="border-color: #000;border-width: 1px;margin-bottom: 20px;color: #000;" cellpadding="0" cellspacing="0">
            <tbody>
            <tr>
                <td style="font-weight:bold;vertical-align:middle;">
                    借款人：
                </td>
                <td style="vertical-align:middle;">
                    ${borrowerName!''}
                </td>
                <td style="vertical-align:middle;">
                    身份证号：
                </td>
                <td style="vertical-align:middle;">
                    ${borrowerIdNo!''}
                </td>
            </tr>
            <tr>
                <td style="font-weight:bold;vertical-align:middle;">
                    贷款人：
                </td>
                <td style="vertical-align:middle;">
                    ${lenderName!''}
                </td>
                <td style="font-weight:bold;vertical-align:middle;">
                    贷款金额（元）：
                </td>
                <td style="vertical-align:middle;">
                    人民币【${loanPrincipal!''}】元（大写人民币【${loanPrincipalChinese!''}】）
                </td>
            </tr>
            </tbody>
        </table>
    </p>
    <p style="font-size:14px;">
        <span style="">本借款借据项下的权利义务按照甲乙双方签订的《最高债权额合同》执行。</span>
    </p>
    </p>
</p>

</body>
</html>