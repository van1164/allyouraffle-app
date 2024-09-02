import shared
import SwiftUI

@MainActor
class HomeObserver: ObservableObject {
    private var viewModel: HomeViewModel
    var jwt : String
    @Published var raffleList: [RaffleResponse] = []
    @Published var ticketCount: Int = -1
    @Published var loading: Bool = true
    @Published var error: String? = nil

    
    init(viewModel: HomeViewModel, jwt : String) {
        self.viewModel = viewModel
        self.jwt = jwt
//        CommonFlow<AnyObject>(origin: self.viewModel.popularRaffleList).subscribe{[weak self] list in
//            self?.raffleList = list as! [RaffleResponse]}
//        
        self.viewModel.popularRaffleList.subscribe{[weak self] list in
            self?.raffleList = list as! [RaffleResponse]
        }
        self.viewModel.ticketCount.subscribe{[weak self] count in
            self?.ticketCount = count as! Int
        }
        self.viewModel.loading.subscribe{[weak self] loading in
            self?.loading = loading as! Bool
        }
        self.viewModel.error.subscribe{[weak self] error in
            self?.error = error as! String?
        }
        
        initHome(jwt : self.jwt)
    }
    
    func refresh(jwt:String) async {
        Task {
            do {
                try await viewModel.refresh(jwt: jwt)
            } catch {
                // Handle error
            }
        }
    }

//    func showAd(){
//        Task {
//            do {
//                try await viewModel
//            } catch {
//                // Handle error
//            }
//        }
//    }
    
    func initRaffle() {
        Task {
            do {
                try await viewModel.loadPopularRaffleList()
            } catch {
                // Handle error
            }
        }
    }
    
    func initHome(jwt: String) {
        Task {
            do {
                try await viewModel.doInitHome(jwt: jwt)
            } catch {
                // Handle error
            }
        }
    }

    func loadTickets(jwt: String) async {

            do {
                try await viewModel.loadTickets(jwt: jwt)
            } catch {
                // Handle error
            }
        
    }
    func setError() {
        viewModel.setNullError()
    }
}
