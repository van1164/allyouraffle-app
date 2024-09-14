import shared


class RaffleHistoryObserver: ObservableObject,BaseObserver {

    private var viewModel = RaffleHistoryViewModel()
    @Published var loading : Bool = false
    @Published var error : String? = nil
    @Published var purchaseHistoryList : [PurchaseHistory] = []
    
    init() {
        viewModel.loading.subscribe{[weak self] load in
            self?.loading = load as! Bool
        }
        
        viewModel.error.subscribe{[weak self] err in
            self?.error = err as! String?
        }
        viewModel.purchaseHistory.subscribe{[weak self] purchaseHistoryList in
            self?.purchaseHistoryList = purchaseHistoryList as! [PurchaseHistory]
        }
    }
    
    func loadHistory(){
        Task{
            do{
                if let jwt = loadJwt(){
                    try await viewModel.loadHistory(jwt:jwt)
                }
            }catch{
                
            }
        }
    }
    
    func initHistory(){
        Task{
            do{
                if let jwt = loadJwt(){
                    try await viewModel.doInitHistory(jwt:jwt)
                }
            }catch{
                
            }
        }
    }
    
    func setError(message: String) {
        viewModel.setError(message: message)
    }
    
    func setErrorNull() {
        viewModel.setNullError()
    }
}
