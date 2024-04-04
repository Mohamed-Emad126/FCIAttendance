function addEditorToSheets() {
  // Get the user's email address.
  var user = Session.getActiveUser();

  var ARRAY_OF_SUBJECTS = [
    "تراكيب محددة - الفرقة الاولى ذكاء",
    "بحوث العمليات - الفرقة الاولى ذكاء",
    "رياضة 2 - الفرقة الاولى ذكاء",
    "برمجة 2 - الفرقة الاولى ذكاء",
    "التصميم المنطقي - الفرقة الاولى ذكاء",
    "القضايا الاجتماعية - الفرقة الاولى ذكاء",
    "تحليل وتصميم الخوارزميات - الفرقة الثانية ذكاء",
    "مقدمة في الذكاء الاصطناعي - الفرقة الثانية ذكاء",
    "مقدمة في علوم البيانات - الفرقة الثانية ذكاء",
    "هندسة البرمجيات - الفرقة الثانية ذكاء",
    "شبكات الحاسب - الفرقة الثانية ذكاء",
    "التسويق الرقمي - الفرقة الثانية ذكاء",
    "الخوارزميات الموزعة والمتزامنة - الفرقة الثالثة ذكاء",
    "عملاء أذكياء - الفرقة الثالثة ذكاء",
    "مهارات الاتصال والتفاوض - الفرقة الثالثة ذكاء",
    "الرؤية بالحاسب - الفرقة الثالثة ذكاء",
    "الذكاء الحسابي - الفرقة الثالثة ذكاء",
    "تحليل بيانات ضخمة - الفرقة الثالثة ذكاء",
    "تحليلات الويب - الفرقة الرابعة ذكاء",
    "تطوير تطبيقات المحمول - الفرقة الرابعة ذكاء",
    "عرض مرئي للبيانات - الفرقة الرابعة ذكاء",
    "الشبكات العصبية والتعلم العميق - الفرقة الرابعة ذكاء",
    "الإنسان الآلي - الفرقة الرابعة ذكاء",
    "تراكيب محددة - الفرقة الاولى عام",
    "بحوث العمليات - الفرقة الاولى عام",
    "رياضة 2 - الفرقة الاولى عام",
    "برمجة 2 - الفرقة الاولى عام",
    "التصميم المنطقي - الفرقة الاولى عام",
    "القضايا الاجتماعية - الفرقة الاولى عام",
    "حقوق الإنسان - الفرقة الاولى عام",
    "تحليل وتصميم الخوارزميات - الفرقة الثانية عام",
    "الرسم بالحاسب - الفرقة الثانية عام",
    "تحليل وتصميم النظم - الفرقة الثانية عام",
    "شبكات الحاسب - الفرقة الثانية عام",
    "الذكاء الاصطناعي - الفرقة الثانية عام",
    "السلوك التنظيمي - الفرقة الثانية عام",
    "إدارة مشروعات البرمجيات - الفرقة الثالثة",
    "مقدمة في إنترنت الأشياء - الفرقة الثالثة",
    "أساسيات الإقتصاد - الفرقة الثالثة",
    "نظم إدارة قواعد البيانات - الفرقة الثالثة - نظم المعلومات",
    "نظم المعلومات الجغرافية - الفرقة الثالثة - نظم المعلومات",
    "الأنظمة الذكية وذكاء الأعمال - الفرقة الثالثة - نظم المعلومات",
    "الشبكات اللاسلكية والمتحركة - الفرقة الثالثة - تكنولوجيا المعلومات",
    "معالجة الإشارات الرقمية - الفرقة الثالثة - تكنولوجيا المعلومات",
    "المتحكمات الصغيرة - الفرقة الثالثة - تكنولوجيا المعلومات",
    "اللغات الصورية - الفرقة الثالثة - علوم الحاسب",
    "التفاعل بين الإنسان والآلة - الفرقة الثالثة - علوم الحاسب",
    "تعلم الآلة - الفرقة الثالثة - علوم الحاسب",
    "بحوث العمليات المتقدمة - الفرقة الثالثة - دعم القرار",
    "النمذجة والمحاكاة - الفرقة الثالثة - دعم القرار",
    "منهجيات دعم القرار - الفرقة الثالثة - دعم القرار",
    "نظم قواعد البيانات الموزعة - الفرقة الرابعة - نظم المعلومات",
    "التنقيب في البيانات وتعليم الآلة - الفرقة الرابعة - نظم المعلومات",
    "استرجاع البيانات - الفرقة الرابعة - نظم المعلومات",
    "هندسة البرمجيات - الفرقة الرابعة - نظم المعلومات",
    "تحليلات الويب والوسائط المجتمعية - الفرقة الرابعة - نظم المعلومات",
    "أمان الشبكات - الفرقة الرابعة - تكنولوجيا المعلومات",
    "موضوعات مختارة 2 - الفرقة الرابعة - تكنولوجيا المعلومات",
    "الشبكات السحابية - الفرقة الرابعة - تكنولوجيا المعلومات",
    "الرؤية بالحاسب - الفرقة الرابعة - تكنولوجيا المعلومات",
    "الإنسان الآلي - الفرقة الرابعة - تكنولوجيا المعلومات",
    "برمجة النظم - الفرقة الرابعة - علوم الحاسب",
    "الخوارزميات الموزعة والمتزامنة - الفرقة الرابعة - علوم الحاسب",
    "موضوعات مختارة - الفرقة الرابعة - علوم الحاسب",
    "تصميم المترجمات - الفرقة الرابعة - علوم الحاسب",
    "نظرية الحسابات - الفرقة الرابعة - علوم الحاسب",
    "إدارة مشروعات المتقدمة - الفرقة الرابعة - دعم القرار",
    "موضوعات مختارة - الفرقة الرابعة - دعم القرار",
    "تحليل القرار - الفرقة الرابعة - دعم القرار",
    "نماذج وطرق الأمثلة - الفرقة الرابعة - دعم القرار",
    "برمجة تحليل البيانات - الفرقة الرابعة - دعم القرار",
    "إدارة مشروعات - الفرقة الثالثة - معلوماتية طبية",
    "مهارات الاتصال والتفاوض - الفرقة الثالثة - معلوماتية طبية",
    "معلوماتية الصحة العامة - الفرقة الثالثة - معلوماتية طبية",
    "نظم دعم القرار - الفرقة الثالثة - معلوماتية طبية",
    "علم العقاقير وتطوير الأدوية - الفرقة الثالثة - معلوماتية طبية",
    "معالجة الإشارات الطبية - الفرقة الثالثة - معلوماتية طبية",
    "مستودعات البيانات الطبية - الفرقة الرابعة - معلوماتية طبية",
    "تكنولوجيا الطب عن بعد - الفرقة الرابعة - معلوماتية طبية",
    "تحسين المعالجة في الرعاية الصحية - الفرقة الرابعة - معلوماتية طبية",
    "تفاعل بين الإنسان والآلة - الفرقة الرابعة - معلوماتية طبية",
    "تحليلات وتمثيل البيانات في الرعاية الصحية - الفرقة الرابعة - معلوماتية طبية"
  ]
  var SPREADSHEET_IDs = [
    "15tvrw9GrcP-zyqB224YP98zOy2-a5TVj4xRCFq0e-GI",
    "15tvrw9GrcP-zyqB224YP98zOy2-a5TVj4xRCFq0e-GI",
    "15tvrw9GrcP-zyqB224YP98zOy2-a5TVj4xRCFq0e-GI",
    "15tvrw9GrcP-zyqB224YP98zOy2-a5TVj4xRCFq0e-GI",
    "15tvrw9GrcP-zyqB224YP98zOy2-a5TVj4xRCFq0e-GI",
    "15tvrw9GrcP-zyqB224YP98zOy2-a5TVj4xRCFq0e-GI",
    "1WNR9TQ2nY_giNWow_bKxnS3yR3Xzl04sdEhaFczAYqQ",
    "1WNR9TQ2nY_giNWow_bKxnS3yR3Xzl04sdEhaFczAYqQ",
    "1WNR9TQ2nY_giNWow_bKxnS3yR3Xzl04sdEhaFczAYqQ",
    "1WNR9TQ2nY_giNWow_bKxnS3yR3Xzl04sdEhaFczAYqQ",
    "1WNR9TQ2nY_giNWow_bKxnS3yR3Xzl04sdEhaFczAYqQ",
    "1WNR9TQ2nY_giNWow_bKxnS3yR3Xzl04sdEhaFczAYqQ",
    "1F4BmlnGTGVW97DNKFs4VW0WFVPkWLNB2zruE-IsPaUo",
    "1F4BmlnGTGVW97DNKFs4VW0WFVPkWLNB2zruE-IsPaUo",
    "1F4BmlnGTGVW97DNKFs4VW0WFVPkWLNB2zruE-IsPaUo",
    "1F4BmlnGTGVW97DNKFs4VW0WFVPkWLNB2zruE-IsPaUo",
    "1F4BmlnGTGVW97DNKFs4VW0WFVPkWLNB2zruE-IsPaUo",
    "1F4BmlnGTGVW97DNKFs4VW0WFVPkWLNB2zruE-IsPaUo",
    "1uUjtmNdPMti-V8GQvQ0y5x5LyA4CPqng5zo_aLI3_cE",
    "1uUjtmNdPMti-V8GQvQ0y5x5LyA4CPqng5zo_aLI3_cE",
    "1uUjtmNdPMti-V8GQvQ0y5x5LyA4CPqng5zo_aLI3_cE",
    "1uUjtmNdPMti-V8GQvQ0y5x5LyA4CPqng5zo_aLI3_cE",
    "1uUjtmNdPMti-V8GQvQ0y5x5LyA4CPqng5zo_aLI3_cE",
    "1hXSrJVB4saYT_hQHj81-pMXX_Nb5zFLcKIDqgSXIS7c",
    "1hXSrJVB4saYT_hQHj81-pMXX_Nb5zFLcKIDqgSXIS7c",
    "1hXSrJVB4saYT_hQHj81-pMXX_Nb5zFLcKIDqgSXIS7c",
    "1hXSrJVB4saYT_hQHj81-pMXX_Nb5zFLcKIDqgSXIS7c",
    "1hXSrJVB4saYT_hQHj81-pMXX_Nb5zFLcKIDqgSXIS7c",
    "1hXSrJVB4saYT_hQHj81-pMXX_Nb5zFLcKIDqgSXIS7c",
    "1hXSrJVB4saYT_hQHj81-pMXX_Nb5zFLcKIDqgSXIS7c",
    "1xi10wdmqt6r92gu2-m5hXI_PK9UsEyZbhLgVuscazjk",
    "1xi10wdmqt6r92gu2-m5hXI_PK9UsEyZbhLgVuscazjk",
    "1xi10wdmqt6r92gu2-m5hXI_PK9UsEyZbhLgVuscazjk",
    "1xi10wdmqt6r92gu2-m5hXI_PK9UsEyZbhLgVuscazjk",
    "1xi10wdmqt6r92gu2-m5hXI_PK9UsEyZbhLgVuscazjk",
    "1xi10wdmqt6r92gu2-m5hXI_PK9UsEyZbhLgVuscazjk",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1T0alB4i5Zy3CGdq-bCRUfhTnV8hW_VOfgUe_gCUyEds",
    "1RQ5CTFFAHpNwvUIlMkq5qTMknUdgRKfEWy98f671j2o",
    "1RQ5CTFFAHpNwvUIlMkq5qTMknUdgRKfEWy98f671j2o",
    "1RQ5CTFFAHpNwvUIlMkq5qTMknUdgRKfEWy98f671j2o",
    "1RQ5CTFFAHpNwvUIlMkq5qTMknUdgRKfEWy98f671j2o",
    "1RQ5CTFFAHpNwvUIlMkq5qTMknUdgRKfEWy98f671j2o",
    "1oNxd4n3-aAr69-LEyDzbICKUgXjfwwjNUqpj-y1SLrQ",
    "1oNxd4n3-aAr69-LEyDzbICKUgXjfwwjNUqpj-y1SLrQ",
    "1oNxd4n3-aAr69-LEyDzbICKUgXjfwwjNUqpj-y1SLrQ",
    "1oNxd4n3-aAr69-LEyDzbICKUgXjfwwjNUqpj-y1SLrQ",
    "1oNxd4n3-aAr69-LEyDzbICKUgXjfwwjNUqpj-y1SLrQ",
    "1y1fPYZa93r2YAOT-t6VVnqV3dAUXHCxgh0SHlvheiNg",
    "1y1fPYZa93r2YAOT-t6VVnqV3dAUXHCxgh0SHlvheiNg",
    "1y1fPYZa93r2YAOT-t6VVnqV3dAUXHCxgh0SHlvheiNg",
    "1y1fPYZa93r2YAOT-t6VVnqV3dAUXHCxgh0SHlvheiNg",
    "1y1fPYZa93r2YAOT-t6VVnqV3dAUXHCxgh0SHlvheiNg",
    "1OSmeFnZaJPpHwYoiTQb_Z_zsd3bu8DtDHm_A7jZnboA",
    "1OSmeFnZaJPpHwYoiTQb_Z_zsd3bu8DtDHm_A7jZnboA",
    "1OSmeFnZaJPpHwYoiTQb_Z_zsd3bu8DtDHm_A7jZnboA",
    "1OSmeFnZaJPpHwYoiTQb_Z_zsd3bu8DtDHm_A7jZnboA",
    "1OSmeFnZaJPpHwYoiTQb_Z_zsd3bu8DtDHm_A7jZnboA",
    "1Gh7RDpjwxdzNjy2cJ-RE3FKVK-jYC1JKhpvWCDw2Qyo",
    "1Gh7RDpjwxdzNjy2cJ-RE3FKVK-jYC1JKhpvWCDw2Qyo",
    "1Gh7RDpjwxdzNjy2cJ-RE3FKVK-jYC1JKhpvWCDw2Qyo",
    "1Gh7RDpjwxdzNjy2cJ-RE3FKVK-jYC1JKhpvWCDw2Qyo",
    "1Gh7RDpjwxdzNjy2cJ-RE3FKVK-jYC1JKhpvWCDw2Qyo",
    "1Gh7RDpjwxdzNjy2cJ-RE3FKVK-jYC1JKhpvWCDw2Qyo",
    "1L-oOe6D0sUvuq4PeEnkiEQ5kg_h0w1hbHUMXp3kqtt4",
    "1L-oOe6D0sUvuq4PeEnkiEQ5kg_h0w1hbHUMXp3kqtt5",
    "1L-oOe6D0sUvuq4PeEnkiEQ5kg_h0w1hbHUMXp3kqtt6",
    "1L-oOe6D0sUvuq4PeEnkiEQ5kg_h0w1hbHUMXp3kqtt7",
    "1L-oOe6D0sUvuq4PeEnkiEQ5kg_h0w1hbHUMXp3kqtt8"
  ]

  var form = FormApp.getActiveForm();

  for(var j = 0; j<form.getResponses().length;j++){
      var lastResponse = form.getResponses()[j];
    var userEmail = lastResponse.getRespondentEmail();
    console.log(userEmail)
    var subjectList = lastResponse.getItemResponses()[1].getResponse();
    // Split the comma-separated subject string into an array.
      if (!subjectList) {
      console.log("No subjects selected in the form.");
      return;
    }

    // Find the spreadsheet IDs for the chosen subjects.
    var spreadsheetIds = [];
    for (var i = 0; i < subjectList.length; i++) {
      var subject = subjectList[i].trim();
      var index = ARRAY_OF_SUBJECTS.indexOf(subject);
      if (index != -1) {
        spreadsheetIds.push(SPREADSHEET_IDs[index]);
      }
    }
    // Add the user as an editor to each spreadsheet.
    for (var i = 0; i < spreadsheetIds.length; i++) {
      var spreadsheetId = spreadsheetIds[i];
      var sheet = SpreadsheetApp.openById(spreadsheetId)
      sheet.addEditor(userEmail);
    }
  }

}
