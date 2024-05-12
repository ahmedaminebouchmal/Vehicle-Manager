package com.technoapps.vehiclemanager.common

object CommonConstants {

    //Todo------------------------------------API - Service ------------------------------------
    //Todo------------------------API - GET-------------------
    val GetVehicleMaster = AppController.BaseUrl.plus("get_vehicle_master")
    val GetRefuelApi = AppController.BaseUrl.plus("get_refuel_api")
    val GetServiceApi = AppController.BaseUrl.plus("get_service_api")
    val GetExpenseMaster = AppController.BaseUrl.plus("get_expense_api")
    val GetInsuranceApi = AppController.BaseUrl.plus("get_insurance_api")
    val GetPermitApi = AppController.BaseUrl.plus("get_permit_api")
    val GetPUCApi = AppController.BaseUrl.plus("get_puc_api")
    val GetAccidentApi = AppController.BaseUrl.plus("get_accident_api")
    val GetSummaryApi = AppController.BaseUrl.plus("get_summary")
    val GetVehicleData = AppController.BaseUrl.plus("get_vehicle_data")

    //Todo------------------------API - SET-------------------
    val SetVehicleMasterApi = AppController.BaseUrl.plus("set_vehicle_master_api")
    val SetRefuelApi = AppController.BaseUrl.plus("set_refuel_api")
    val SetServiceApi = AppController.BaseUrl.plus("set_service_api")
    val SetExpenseApi = AppController.BaseUrl.plus("set_expense_api")
    val SetInsuranceApi = AppController.BaseUrl.plus("set_insurance_api")
    val SetPermitApi = AppController.BaseUrl.plus("set_permit_api")
    val SetPUCApi = AppController.BaseUrl.plus("set_puc_api")
    val SetAccidentApi = AppController.BaseUrl.plus("set_accident_api")
    val SetVehicleDelete = AppController.BaseUrl.plus("vehicle_delete")


    //Todo------------------------Messages ------------------------
    const val MsgSignInSuccessfully = "Sign in successfully..."
    const val MsgYouAreSignOut = "You are now sign out..."
    const val MsgSomethingWrong = "Something went wrong. Please try again!"
    const val MsgSignInCancelled = "Sign in cancelled..."
    const val MsgVehicleTypeRequired = "Vehicle type is required"
    const val MsgVehicleBrandRequired = "Vehicle brand is required"
    const val MsgVehicleModelRequired = "Vehicle Model is required"
    const val MsgVehicleBuildYearRequired = "Vehicle Build Year is required"
    const val MsgEnterCorrectBuildYear = "Enter Correct Build Year"
    const val MsgVehicleRegNoRequired = "Vehicle Reg. No is required"
    const val MsgVehicleFuelTypeRequired = "Vehicle Fuel Type is required"
    const val MsgVehiclePurchasePriceRequired = "Vehicle Purchase Price is required"

    const val MsgFuelDateRequired = "Fuel Date is required"
    const val MsgFuelTypeRequired = "Fuel type is required"
    const val MsgFuelTotalAmountRequired = "Fuel total amount is required"

    const val MsgTotalAmtLessThanPrice = "Total amount should not be less than fuel price"
    const val MsgFuelPriceRequired = "Fuel price is required"
    const val MsgVehicleKmRequired = "Km reading is required"

    const val MsgServiceDateRequired = "Service Date is required"
    const val MsgServiceTotalAmountRequired = "Service total amount is required"
    const val MsgSelectAnyService = "Please Select any service"

    const val MsgExpenseDateRequired = "Expense Date is required"
    const val MsgExpenseTypeRequired = "Expense Type is required"
    const val MsgExpenseTotalAmountRequired = "Expense total amount is required"

    const val MsgInsuranceCompanyRequired = "Insurance company name is required"
    const val MsgInsurancePolicyTypeRequired = "Insurance policy type is required"
    const val MsgInsurancePolicyNoRequired = "Insurance policy no is required"
    const val MsgInsuranceIssueDateRequired = "Insurance issue date is required"
    const val MsgInsuranceExpiryDateRequired = "Insurance expiry date is required"
    const val MsgInsurancePaymentModeRequired = "Insurance payment mode is required"
    const val MsgInsuranceAmountRequired = "Insurance amount is required"
    const val MsgInsurancePremiumRequired = "Insurance premium is required"

    const val MsgPermitPolicyTypeRequired = "Permit type is required"
    const val MsgPermitIssueDateRequired = "Permit issue date is required"
    const val MsgPermitExpiryDateRequired = "Permit expiry date is required"
    const val MsgPermitNoRequired = "Permit no is required"
    const val MsgPermitCostRequired = "Permit cost is required"

    const val MsgPUCNoRequired = "PUC no is required"
    const val MsgPUCIssueDateRequired = "PUC issue date is required"
    const val MsgPUCExpiryDateRequired = "PUC expiry date is required"
    const val MsgPUCAmountRequired = "PUC amount is required"

    const val MsgAccidentDateRequired = "Accident Date is required"
    const val MsgAccidentTimeRequired = "Accident Time is required"
    const val MsgAccidentAmountRequired = "Accident amount is required"
    const val MsgAccidentKmRequired = "Km reading is required"

    const val MsgAllowPermission = "Please allow all required permission to continue..."
    const val MsgNoNetwork = "No network. Please check your internet connection."
    const val MsgRecordAddedSuccessfully = "Record Added Successfully."
    const val MsgRecordUpdatedSuccessfully = "Record Updated Successfully."
    const val MsgRecordDeletedSuccessfully = "Record Deleted Successfully."
    const val MsgDoYouWantToExit = "Are you sure do you want to exit?"
    const val MsgDoYouWantToClear = "Are you sure do you want to clear data?"
    const val MsgDoYouWantToDeleteItem = "Are you sure do you want to delete vehicle all information?"
    const val MsgDoubleBackToExit = "Click back again to Exit..."


    //Todo------------------------Code For Request & Result Handling------------------------
    const val RequestDataUpdated = 111
    const val KeyIsDataUpdated = "IsDataUpdated"
    const val RC_GOOGLE_SIGN_IN = 222

    //Todo------------------------For Preferences-----------------------------------
    const val KeyDeviceId = "DeviceId"
    const val KeyEmailId = "EmailId"

    //Todo------------------------Passing Data between Activities------------------------
    const val KeyIsFirstTime = "IsFirstTime"
    const val KeyVehicleId = "VehicleId"
    const val KeyVehicleMinDate = "VehicleMinDate"
    const val KeyVehicleDetail = "VehicleDetail"
    const val KeyIsFuelType = "FuelType"
    const val KeyRefuelDetail = "RefuelDetail"
    const val KeyServiceDetail = "ServiceDetail"
    const val KeyExpenseDetail = "ExpenseDetail"
    const val KeyInsuranceDetail = "InsuranceDetail"
    const val KeyPermitDetail = "PermitDetail"
    const val KeyPUCDetail = "PUCDetail"
    const val KeyAccidentDetail = "AccidentDetail"
    const val KeyActionTypeNew = "new"
    const val KeyActionTypeUpdate = "update"

//    const val VehicleId = "VehicleId"
//    const val VehicleType = "VehicleType"
//    const val VehicleTitle = "VehicleTitle"
//    const val VehicleBrand = "VehicleBrand"
//    const val VehicleModel = "VehicleModel"
//    const val VehicleBuildYear = "VehicleBuildYear"
//    const val VehicleRegistrationNo = "VehicleRegistrationNo"
//    const val VehicleFuelType = "VehicleFuelType"
//    const val VehicleTankCapacity = "VehicleTankCapacity"
//    const val VehicleDisplayName = "VehicleDisplayName"
//    const val VehiclePurchaseDate = "VehiclePurchaseDate"
//    const val VehiclePurchasePrice = "VehiclePurchasePrice"
//    const val VehicleKmReading = "VehicleKmReading"


    //Todo------------------------Caption Or Hints For Views-----------------------------------
    const val CapDateFormat = "yyyy-MM-dd"

    const val CapServerDateFormat = "yyyy-MM-dd hh:mm:ss"

    const val CapDateWithMonthName = "MMM-dd-yyyy"
    const val CapTimeShortFormat = "hh:mm a"
    const val CapTimeFullFormat = "HH:mm"

    const val CapPleaseWait = "Please wait..."
    const val CapPermission = "Permission"
    const val CapContinue = "Continue"
    const val CapCancel = "Cancel"
    const val CapConfirm = "Confirm"
    const val CapUpdate = "Update"
    const val CapSelect = "Select"
    const val CapAdd = "Add"
    const val CapDelete = "Delete"
    const val CapMessage = "Message"
    const val CapYes = "Yes"
    const val CapNo = "No"
    const val CapExit = "Exit"
    const val CapClear = "Clear"


    var EXIT_BTN_COUNT = "exit_btn_count"

    const val IS_FIRST_TIME = "is_first_time"
    var SPLASH_SCREEN_COUNT = "splash_screen_count"
    var GOOGLE_BANNER = "GOOGLE_BANNER"
    var GOOGLE_INTERSTITIAL = "GOOGLE_INTERSTITIAL"
    var GOOGLE_REWARDED_VIDEO = "GOOGLE_REWARDED_VIDEO"

    var FB_BANNER = "FB_BANNER"
    var FB_BANNER_RECTANGLE_AD = "FB_BANNER_RECTANGLE_AD"
    var FB_INTERSTITIAL = "FB_INTERSTITIAL"
    var FB_REWARDED_VIDEO = "FB_REWARDED_VIDEO"

//    var STATUS_ENABLE_DISABLE = "STATUS_ENABLE_DISABLE"
    var AD_TYPE_FB_GOOGLE = "AD_TYPE_FB_GOOGLE"
//
//    var AD_FACEBOOK = "facebook"
//    var AD_GOOGLE = "google"
//    const val ENABLE = "Enable"

    var FB_AD_IS_LOADED: Boolean = true
    var GOOGLE_AD_IS_LOADED: Boolean = true



    var STATUS_ENABLE_DISABLE = "STATUS_ENABLE_DISABLE"



    const val GOOGLE_ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val GOOGLE_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val GOOGLE_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"


    const val FB_BANNER_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"
    const val FB_INTERSTITIAL_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"


    var AD_FACEBOOK = "facebook"
    var AD_GOOGLE = "google"
    var GOOGLE_FACEBOOK_Ad= AD_GOOGLE
//    val AD_TYPE_FB_GOOGLE: String = AD_GOOGLE


    const val ENABLE = "Enable"
    const val DISABLE = "Disable"
    val ENABLE_DISABLE: String = ENABLE
}