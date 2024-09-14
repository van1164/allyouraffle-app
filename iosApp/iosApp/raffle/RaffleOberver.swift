import shared
import SwiftUI

@MainActor
class RaffleObserver: ObservableObject {
    private var viewModel: RaffleViewModel
    @Published var raffleList: [RaffleResponse] = []
    @Published var ticketCount: Int = -1
    @Published var loading : Bool = false
    @Published var error : String? = nil
    var isFree : Bool
    
    init(isFree: Bool, raffleViewModel: RaffleViewModel, jwt: String) {
        self.isFree = isFree
        self.viewModel = raffleViewModel
        self.viewModel.raffleList.subscribe{[weak self] list in
            self?.raffleList = list as? [RaffleResponse] ?? []
        }
        self.viewModel.ticketCount.subscribe{[weak self] ticketCount in
            self?.ticketCount = ticketCount as! Int
        }
        
        self.viewModel.loading.subscribe{[weak self] loading in
            self?.loading = loading as! Bool
        }
        
        self.viewModel.error.subscribe{[weak self] error in
            self?.error = error as! String?
        }
        
        initRaffle(isFree: isFree)
        loadTickets(jwt: jwt)
    }
    
    
    func initRaffle(isFree: Bool) {
        Task {
            do {
                try await viewModel.doInitRaffle(isFree: isFree)
            } catch {
                // Handle error
            }
        }
    }
    
    func loadTickets(jwt: String) {
        Task{  
            do {
                try await viewModel.loadTickets(jwt: jwt)
            } catch {
                // Handle error
            }
        }
    }
    
    func setError(message : String) {
        viewModel.setError(message: message)
    }
    
    func setErrorNull(){
        viewModel.setNullError()
    }
}
