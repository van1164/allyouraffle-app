import SwiftUI
import shared
import Kingfisher

struct RaffleHistoryView: View {
    @ObservedObject var observer: RaffleHistoryObserver = RaffleHistoryObserver()
    @Environment(\.presentationMode) var presentationMode
    @State private var isRefreshing = false
    
    var body: some View {
        VStack {
            if observer.loading {
                LoadingScreen()
            } else {
                VStack {
                    Text("래플 이력")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(Color("Main"))
                        .padding()
                    
                    ScrollView {
                        LazyVStack {
                            ForEach(observer.purchaseHistoryList, id: \.raffle.id) { raffle in
                                RaffleCard(purchaseHistory: raffle)
                            }
                        }
                    }
                    .refreshable {
                        observer.initHistory()
                    }
                    
                    if observer.loading {
                        ProgressView() // 로딩 인디케이터
                            .padding()
                    } else {
                        GeometryReader { geometry in
                            Color.clear
                                .onAppear {
                                    if geometry.frame(in: .global).maxY < UIScreen.main.bounds.height {
                                        observer.loadHistory() // 데이터 로드 함수 호출
                                    }
                                }
                        }
                        .frame(height: 40)
                    }
                }
            }
        }
        .onAppear {
            observer.initHistory()
        }
    }
    
    private var backButton: some View {
        Button(action: {
            presentationMode.wrappedValue.dismiss()
        }) {
            HStack {
                Image(systemName: "chevron.left")
                Text("마이 페이지로")
            }
        }
    }
}

struct RaffleCard: View {
    var purchaseHistory: PurchaseHistory
    
    var body: some View {
        VStack {
            HStack {
                ZStack(alignment: .center){
                    KFImage(URL(string: purchaseHistory.raffle.item.imageUrl))
                        .placeholder{
                            ProgressView()
                        }
                        .resizable()
                        .aspectRatio(1,contentMode: .fit)
                        .frame(width: 100, height: 100)
                        .colorMultiply(.gray)
                        .blur(radius: 3)
                        .clipped()
                        .padding()
                    
                    if purchaseHistory.raffle.winner == nil {
                        Text("추첨전")
                            .foregroundColor(.black)
                            .font(.system(size: 30, weight: .bold))
                            .lineLimit(1)
                            .frame(width: 100, height: 100,alignment: .center)
                    } else if purchaseHistory.isWinner {
                        Text("당첨")
                            .foregroundColor(.green)
                            .font(.system(size: 30, weight: .bold))
                            .lineLimit(1)
                            .frame(width: 100, height: 100,alignment: .center)
                    } else {
                        Text("낙첨")
                            .foregroundColor(.red)
                            .font(.system(size: 30, weight: .bold))
                            .lineLimit(1)
                            .frame(width: 100, height: 100,alignment: .center)
                    }
                }
                VStack(alignment: .leading) {
                    Text(purchaseHistory.raffle.item.name)
                        .font(.headline)
                    Text("응모 개수: \(purchaseHistory.count)")
                        .font(.subheadline)
                    Text("래플 고유 ID: \(purchaseHistory.raffle.id)")
                        .font(.footnote)
                        .foregroundColor(.gray)
                    
                    if purchaseHistory.raffle.winner == nil {
                        Text("추첨 전")
                            .foregroundColor(.gray)
                    } else {
                        Text("당첨자: \(purchaseHistory.raffle.winner!.nickname) (\(String(purchaseHistory.raffle.winner!.phoneNumber?.prefix(8).suffix(5) ?? "")))")
                            .font(.footnote)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
            }
            .background(Color.white)
            .cornerRadius(10)
            .shadow(radius: 2)
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal)
    }
}
