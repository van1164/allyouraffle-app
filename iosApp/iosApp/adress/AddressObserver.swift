import shared


class AddressObserver: ObservableObject {
    private var addressViewModel = AddressViewModel()
    @Published var loading : Bool = false
    @Published var error : String? = nil
    @Published var addressInfo : AddressInfo? = nil
    @Published var detail : String? = nil
    
    init(){
        addressViewModel.loading.subscribe{[weak self] load in
            self?.loading = load as! Bool
        }
        
        addressViewModel.error.subscribe{[weak self] err in
            self?.error = err as! String?
        }
        addressViewModel.userAddress.subscribe{[weak self] userAddress in
            self?.addressInfo = userAddress as! AddressInfo?
        }
        addressViewModel.detail.subscribe{[weak self] detail in
            self?.detail = detail as! String?
            
        }
    }
    
    func setUserAddress(address: AddressInfo){
        addressViewModel.setUserAddress(address: address)
    }
    
    func saveUserAddress(jwt: String) -> Bool{
        return addressViewModel.saveUserAddress(jwt: jwt)
    }
    
}
