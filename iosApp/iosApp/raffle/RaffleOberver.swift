import shared
import SwiftUI

class RaffleObserver: ObservableObject {
    @Published var raffleList: [RaffleResponse] = []
    @Published var isLoading: Bool = false
    @Published var raffleViewModel = RaffleViewModel()
    var isFree : Bool
    
    init(isFree: Bool) {
        self.isFree = isFree
        isLoading = true
        defer { isLoading = false }
        raffleViewModel.doInitRaffle(isFree: isFree)
        fetch()

    }
    
    func getRaffleList() -> [RaffleResponse]{
        return self.raffleList
    }
    
    func fetch() {
        print("FETCH")
        raffleViewModel.raffleList.value.publisher.sink{ [weak self] item in
            self?.raffleList = item as! [RaffleResponse]
        }
    }
    
    func fetchRaffleList(){
        raffleViewModel.loadRaffles(isFree: self.isFree)
        fetch()
    }
}
