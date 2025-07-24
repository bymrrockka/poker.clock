package by.mrrockka.service;

import by.mrrockka.domain.bounty.Bounty;
import by.mrrockka.mapper.BountyMapper;
import by.mrrockka.repo.bounty.BountyRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Deprecated(forRemoval = true)
@RequiredArgsConstructor
public class BountyService {

  private final BountyRepository bountyRepository;
  private final BountyMapper bountyMapper;

  public void storeBounty(final UUID gameId, final by.mrrockka.domain.Bounty bounty, final Instant createdAt) {
    bountyRepository.save(gameId, bounty.getFrom(), bounty.getTo(), bounty.getAmount(), createdAt);
  }

  public void storeBounty(final UUID gameId, final Bounty bounty, final Instant createdAt) {
    bountyRepository.save(gameId, bounty.from().getId(), bounty.to().getId(), bounty.amount(), createdAt);
  }

  public List<Bounty> getAllForGame(@NonNull final UUID gameId) {
    final var bounties = bountyRepository.findAllByGameId(gameId);

    return bounties.stream()
      .map(bountyMapper::toDomain)
      .toList();
  }


}
