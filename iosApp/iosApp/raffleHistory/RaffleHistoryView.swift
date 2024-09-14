//import SwiftUI
//
//struct RaffleHistoryView: View {
//    @ObservedObject var viewModel: RaffleHistoryViewModel
//    @Environment(\.presentationMode) var presentationMode
//    @State private var isRefreshing = false
//
//    var body: some View {
//        NavigationView {
//            VStack {
//                if viewModel.error != nil {
//                    Text(viewModel.error!)
//                        .foregroundColor(.red)
//                        .padding()
//                }
//
//                Text("래플 이력")
//                    .font(.largeTitle)
//                    .fontWeight(.bold)
//                    .padding()
//
//                if viewModel.loading {
//                    ProgressView()
//                } else {
//                    List {
//                        ForEach(viewModel.purchaseHistory, id: \.id) { purchase in
//                            RaffleCard(purchaseHistory: purchase)
//                        }
//                    }
//                    .refreshable {
//                        await refreshHistory()
//                    }
//                }
//            }
//            .navigationTitle("마이 페이지로")
//            .navigationBarBackButtonHidden(true)
//            .navigationBarItems(leading: backButton)
//        }
//        .onAppear {
//            loadHistory()
//        }
//    }
//
//    private var backButton: some View {
//        Button(action: {
//            presentationMode.wrappedValue.dismiss()
//        }) {
//            HStack {
//                Image(systemName: "chevron.left")
//                Text("마이 페이지로")
//            }
//        }
//    }
//
//    private func loadHistory() {
//        Task {
//            await viewModel.initHistory()
//        }
//    }
//
//    private func refreshHistory() async {
//        isRefreshing = true
//        await viewModel.initHistory()
//        isRefreshing = false
//    }
//}
//
//struct RaffleCard: View {
//    var purchaseHistory: PurchaseHistory
//
//    var body: some View {
//        VStack {
//            HStack {
//                AsyncImage(url: URL(string: purchaseHistory.raffle.item.imageUrl)) { image in
//                    image
//                        .resizable()
//                        .aspectRatio(contentMode: .fill)
//                        .frame(width: 100, height: 100)
//                        .clipped()
//                } placeholder: {
//                    ProgressView()
//                }
//                .padding()
//
//                VStack(alignment: .leading) {
//                    Text(purchaseHistory.raffle.item.name)
//                        .font(.headline)
//                    Text("응모 개수: \(purchaseHistory.count)")
//                        .font(.subheadline)
//                    Text("래플 고유 ID: \(purchaseHistory.raffle.id)")
//                        .font(.footnote)
//                        .foregroundColor(.gray)
//
//                    if purchaseHistory.raffle.winner == nil {
//                        Text("추첨 전")
//                            .foregroundColor(.gray)
//                    } else {
//                        Text("당첨자: \(purchaseHistory.raffle.winner!.nickname) (\(String(purchaseHistory.raffle.winner!.phoneNumber?.prefix(7).suffix(4) ?? "")))")
//                            .font(.footnote)
//                    }
//                }
//                .padding()
//            }
//            .background(Color.white)
//            .cornerRadius(10)
//            .shadow(radius: 2)
//            .padding(.horizontal)
//        }
//    }
//}
