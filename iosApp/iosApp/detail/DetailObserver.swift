import shared
import SwiftUI

@MainActor
class DetailObserver: ObservableObject {
    private var viewModel: RaffleDetailViewModel
    @Published var loading : Bool = false
    @Published var error : String? = nil
    @Published var raffleDetail: RaffleDetailResponse? = nil
    @Published var ticketCount: Int = -1
    @Published var raffleEnd: Bool = false
    @Published var purchaseSuccess: Bool = false
    @Published var purchaseFail: Bool = false
    var isFree : Bool
    var jwt : String
    var id : String
    
    init(isFree: Bool, jwt: String, id : String) {
        self.isFree = isFree
        self.viewModel = RaffleDetailViewModel()
        self.id = id
        self.jwt = jwt
        self.viewModel.raffleDetail.subscribe{[weak self] raffleDetail in
            self?.raffleDetail = raffleDetail as! RaffleDetailResponse?
        }
        self.viewModel.userTickets.subscribe{[weak self] userTickets in
            self?.ticketCount = userTickets as! Int
        }
        
        self.viewModel.loading.subscribe{[weak self] loading in
            self?.loading = loading as! Bool
        }
        
        self.viewModel.error.subscribe{[weak self] error in
            self?.error = error as! String?
        }
        self.viewModel.purchaseSuccess.subscribe{ [weak self] purchaseSuccess in
            self?.purchaseSuccess = purchaseSuccess as! Bool
        }
        
        self.viewModel.purchaseFail.subscribe{ [weak self] purchaseFail in
            self?.purchaseFail = purchaseFail as! Bool
        }
        self.viewModel.raffleEnd.subscribe{ [weak self] rafflleEnd in
            self?.raffleEnd = rafflleEnd as! Bool
        }
        
    }
    
    
    func loadTickets() {
        Task {
            do {
                try await viewModel.loadUserTickets(jwt: jwt)
            } catch {
                // Handle error
            }
        }
    }
    
    func initRaffleDetail() {
        print("Init")
        Task {
            do {
                try await viewModel.doInitRaffleDetail(jwt: jwt, id: id, isFree: isFree)
            } catch {
                // Handle error
            }
        }
    }
    
    func purchaseWithTicket(onFinsish : @escaping () -> (Void)){
        Task {
            do {
                try await viewModel.purchaseWithTicket(jwt: jwt, id: id)
                onFinsish()
            } catch {
                // Handle error
            }
        }
    }
    
    func setSuccessFalse(){
        viewModel.setSuccessFalse()
    }
    
    func setFailFalse(){
        viewModel.setFailFalse()
    }
    
    func setError(message : String){
        viewModel.setError(message: message)
    }
    func setErrorNull(){
        viewModel.setNullError()
    }
}
