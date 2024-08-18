import SwiftUI
import shared

struct RaffleListView: View {
    @ObservedObject var observer: RaffleObserver
    var isFree: Bool

    init(isFree: Bool) {
        self.observer = RaffleObserver(isFree: isFree)
        self.isFree = isFree
    }

    var body: some View {
        NavigationView{
            VStack(alignment:.leading) {
                Logo(fontSize: 60)
                Spacer().frame(height: 10)
                
                Text(isFree ? "광고 래플" : "천원 래플")
                    .font(.custom("Jua",size: 30))
                    .multilineTextAlignment(.trailing)
                    .padding()
                
                ScrollView {
                    LazyVStack {
                        ForEach(observer.raffleList, id: \.id) { raffle in
                            ProductCard(raffle: raffle, viewModel: observer, isFree: isFree)
                        }
                    }
                }.refreshable {
                    observer.fetchRaffleList()
                }
                
            }
        }
    }
}

struct ProductCard: View {
    var raffle: RaffleResponse
    @ObservedObject var viewModel: RaffleObserver
    var isFree: Bool

    var body: some View {
        let rowHeight: CGFloat = 120

        NavigationLink(destination: DetailView(raffleId: String(raffle.id))) {
            HStack {
                AsyncImage(url: URL(string: raffle.item.imageUrl)) { image in
                    image
                        .resizable()
                        .aspectRatio(1,contentMode: .fit)
                        .frame(height: rowHeight)
                        .clipped()
                } placeholder: {
                    Image("error_image") // 오류 이미지
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(height: rowHeight)
                        .clipped()
                }

                RaffleRightColumn(raffle: raffle, viewModel: viewModel, rowHeight: rowHeight)
            }
            .padding(3)
            .background(Color.white)
            .cornerRadius(8)
            .shadow(radius: 2)
        }
    }
}

struct RaffleRightColumn: View {
    var raffle: RaffleResponse
    @ObservedObject var viewModel: RaffleObserver
    var rowHeight: CGFloat

    var body: some View {
        VStack(alignment: .leading) {
            Text(raffle.item.name)
                .font(.custom("Jua",size: 23))
                .foregroundColor(.black)
                .padding(.leading, 2)

            Spacer().frame(height: 15)

            ProgressView(value: Double(raffle.currentCount), total: Double(raffle.totalCount))
                .progressViewStyle(LinearProgressViewStyle(tint: Color.blue))
                .frame(height: 15)
                .padding(.trailing,15)
                .clipShape(RoundedRectangle(cornerRadius: 12))

            Spacer().frame(height: 15)

            Text("\(Int((Double(raffle.currentCount) / Double(raffle.totalCount)) * 100))%")
                .font(.custom("Jua", size: 15))
                .foregroundColor(.black)
                .frame(maxWidth: .infinity, alignment: .trailing)
                .padding(.trailing, 10)
        }
        .frame(height: rowHeight)
        .padding(.leading, 3)
    }
}

struct RaffleListPreview: PreviewProvider {
    static var previews: some View {
        RaffleListView(isFree:true)
    }
}
