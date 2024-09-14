import shared


class PhoneNumberObserver: ObservableObject {
    private var phoneNumberViewModel = PhoneNumberViewModel()
    @Published var loading : Bool = false
    @Published var error : String? = nil
    @Published var phoneNumber : String = ""
    @Published var verifying : Bool = false
    @Published var verifyNumber : String? = nil
    @Published var numberSaved : Bool = false
    
    
    init(){
        phoneNumberViewModel.loading.subscribe{[weak self] load in
            self?.loading = load as! Bool
        }
        
        phoneNumberViewModel.error.subscribe{[weak self] err in
            self?.error = err as! String?
        }
        phoneNumberViewModel.phoneNumber.subscribe{[weak self] phoneNumber in
            self?.phoneNumber = phoneNumber as! String
        }
        phoneNumberViewModel.verifying.subscribe{[weak self] verifying in
            self?.verifying = verifying as! Bool
        }
        phoneNumberViewModel.verifyNumber.subscribe{[weak self] verifyNumber in
            self?.verifyNumber = verifyNumber as! String?
        }
        phoneNumberViewModel.numberSaved.subscribe{[weak self] numberSaved in
            self?.numberSaved = numberSaved as! Bool
        }
    }
    
    func verifyPhoneNumber() {
        phoneNumberViewModel.setPhoneNumber(number: phoneNumber)

        phoneNumberViewModel.verifyPhoneNumber{ data in
            print("XXXXXXXXXXXXX"+(self.verifyNumber?.description ?? "AA"))
        }
    }
    
    func savePhoneNumber(jwt : String){
        phoneNumberViewModel.savePhoneNumber(jwt:jwt){ data in
        }
    }
    func setError(message : String) {
        phoneNumberViewModel.setError(message: message)
    }
    
    func setErrorNull(){
        phoneNumberViewModel.setNullError()
    }
}
